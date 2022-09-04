package io.bao.betterreaddataloader;


import connection.DataStaxAstraProperties;
import io.bao.betterreaddataloader.author.Author;
import io.bao.betterreaddataloader.author.AuthorRepository;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

@SpringBootApplication
@EnableConfigurationProperties(DataStaxAstraProperties.class)
public class BetterReadDataloaderApplication {

    @Autowired AuthorRepository authorRepository;

    @Value("${datadump.location.author}")
    private String authorLocation;


    @Value("${datadump.location.works}")
    private String worksLocation;


    public static void main(String[] args) {
        SpringApplication.run(BetterReadDataloaderApplication.class , args);
    }


    private void initAuthors(){
        Path path = Paths.get(authorLocation);
        try (Stream<String> lines = Files.lines(path)) {
            lines.forEach(line -> {
                String jsonString = line.substring(line.indexOf("{"));

                try {
                    JSONObject jsonObject = new JSONObject(jsonString);

                    Author author = new Author();
                    author.setName(jsonObject.optString("name"));
                    if((jsonObject.opt("personal_name") == null)) {
                        author.setPersonalName("N/A");
                    } else {
                        author.setPersonalName(jsonObject.optString("personal_name"));
                    }
                    author.setId(jsonObject.optString("key").replace("/authors/",""));
                    //System.out.println(author.getId() + ": "  + author.getName() + " -- " + author.getPersonalName());
                    authorRepository.save(author);
                } catch(JSONException e) {
                    e.printStackTrace();
                }

            });
        } catch(IOException e) {
            e.printStackTrace();
        }

    }

    private void initWorks(){
        Path path = Paths.get(worksLocation);
        try (Stream<String> lines = Files.lines(path)) {

            lines.forEach(line -> {
                String jsonString = line.substring(line.indexOf("{"));

                try {
                    JSONObject jsonObject = new JSONObject(jsonString);


                } catch(JSONException e) {
                    e.printStackTrace();
                }

            });

        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    @PostConstruct
    public void start() {
        initAuthors();
        //initWorks();
    }



    @Bean
    public CqlSessionBuilderCustomizer sessionBuilderCustomizer (DataStaxAstraProperties astraProperties) {
        Path bundle = astraProperties.getSecureConnectBundle().toPath();
        return builder -> builder.withCloudSecureConnectBundle(bundle);
    }

}
