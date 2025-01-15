package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.DadosEpisodio;
import br.com.alura.screenmatch.model.DadosSerie;

import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


public class Principal {
    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();

    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=8c350aed";

    public void exibeMenu() {
        System.out.println("Digite o nome da serie");
        var nomesSerie = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + nomesSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        System.out.println(dados);


        //"https://www.omdbapi.com/?t=gilmore+girls&season=1&episode=2&apikey=8c350aed"

        List<DadosTemporada> temporadas = new ArrayList<>();

        for (int i = 1; i <= dados.totalTemporadas(); i++) {
            json = consumo.obterDados(ENDERECO + nomesSerie.replace(" ", "+") + "&season=" + i + API_KEY);
            DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
            temporadas.add(dadosTemporada);

        }

        temporadas.forEach(System.out::println);

//        for (int i = 0; i < dados.totalTemporadas(); i++){
//            List<DadosEpisodio> episodiosTemporadas = temporadas.get(i).episodios();
//            for (int j = 0; j < episodiosTemporadas.size(); j++){
//                System.out.println(episodiosTemporadas.get(j).titulo());
//            }
//        }

        temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));

        List<DadosEpisodio> dadosEpisodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream())
                .collect(Collectors.toList());

//        dadosEpisodios.stream()
//                .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
//                .peek(e -> System.out.println("Primeiro filtro (N/A) " + e))
//                .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
//                .peek(e -> System.out.println("Ordenacao " + e))
//                .limit(10)
//                .peek(e -> System.out.println("Limite " + e))
//                .map(e -> e.titulo().toUpperCase())
//                .peek(e -> System.out.println("Mapeamento ty" + e))
//
//                .forEach(System.out::println);


        List<Episodio> episodios = (List<Episodio>) temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(d -> new Episodio(t.numero(), d))
                ).collect(Collectors.toList());

        episodios.forEach(System.out::println);

        System.out.println("Digite um trecho do titulo do episodio");
        var trechoTitulo = leitura.nextLine();
        Optional<Episodio> episodioBuscado = episodios.stream()
                .filter(e -> e.getTitulo().toUpperCase().contains(trechoTitulo.toUpperCase()))
                .findFirst();
        if (episodioBuscado.isPresent()) {
            System.out.println("Episodio encotrado com sucesso!");
            System.out.println("Temporada: " + episodioBuscado.get().getTemporada());
        } else {
            System.out.println("Episodio nao encontrado!");
        }

//
//        System.out.println("Que ano voce deseja ver os episodios?");
//        var ano = leitura.nextInt();
//        leitura.nextLine();
//
//        LocalDate dataBusca = LocalDate.of(ano,1 , 1);
//
//
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//        episodios.stream()
//                .filter(e -> e.getDatalancamento() != null && e.getDatalancamento().isAfter(dataBusca))
//                .forEach(e -> System.out.println(
//                        "Temporada: " + e.getTemporada() +
//                                " Episodio: " + e.getTitulo() +
//                                " Data lancamento: " + e.getDatalancamento().format(formatter)
//                ));

        Map<Integer, Double> avaliacoesPorTemporada = episodios.stream()
                .filter(e -> e.getAvalicao() > 0.0)
                .collect(Collectors.groupingBy(Episodio::getTemporada,
                        Collectors.averagingDouble(Episodio::getAvalicao)));
        System.out.println(avaliacoesPorTemporada);

        DoubleSummaryStatistics est = episodios.stream()
                .filter(e -> e.getAvalicao() > 0.0)
                .collect(Collectors.summarizingDouble(Episodio::getAvalicao));
        System.out.println("Media: " + est.getAverage());
        System.out.println("Melhor episodio : " + est.getMax());
        System.out.println("Pior episodio : " + est.getMin());
        System.out.println("Quantidade : " + est.getCount());


    }
}
