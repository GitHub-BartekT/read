package pl.iseebugs.doread.domain.sentence;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(value = "sentences")
public record SentencesProperties (List<String> sentences){
}

