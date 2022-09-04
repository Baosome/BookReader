package io.bao.betterreaddataloader;

import connection.DataStaxAstraProperties;
import io.bao.betterreaddataloader.author.Author;
import io.bao.betterreaddataloader.author.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import javax.annotation.PostConstruct;
import java.nio.file.Path;

@SpringBootApplication
@EnableConfigurationProperties(DataStaxAstraProperties.class)
public class BetterReadDataloaderApplication {

    @Autowired AuthorRepository authorRepository;

    public static void main(String[] args) {
        SpringApplication.run(BetterReadDataloaderApplication.class , args);
    }

    @PostConstruct
    public void start() {
        Author author = new Author();
        author.setId("ID");
        author.setName("NAME");
        author.setPersonalName("PERSONAL_NAME");
        authorRepository.save(author);
    }



    @Bean
    public CqlSessionBuilderCustomizer sessionBuilderCustomizer (DataStaxAstraProperties astraProperties) {
        Path bundle = astraProperties.getSecureConnectBundle().toPath();
        return builder -> builder.withCloudSecureConnectBundle(bundle);
    }

}
