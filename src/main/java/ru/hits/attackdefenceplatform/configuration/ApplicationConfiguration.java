package ru.hits.attackdefenceplatform.configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.hits.attackdefenceplatform.configuration.adapter.LocalDateTimeAdapter;

import java.time.LocalDateTime;

@Configuration
public class ApplicationConfiguration {
    @Bean
    public Gson gson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }
}
