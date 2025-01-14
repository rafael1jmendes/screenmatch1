package br.com.alura.screenmatch.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DadosEpisodio(@JsonAlias ("Title") String titulo,
                            @JsonAlias ("Episode") Integer Ep  ,
                            @JsonAlias ("imdbRating") String avalicao,
                            @JsonAlias ("Released") String datalancamento) {
}
