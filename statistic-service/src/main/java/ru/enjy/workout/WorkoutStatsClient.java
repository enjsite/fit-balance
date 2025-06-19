package ru.enjy.workout;

import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.enjy.client.BaseClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;

@Service
public class WorkoutStatsClient extends BaseClient {

    private static final String API_PREFIX = "/api/v1/workout";

    public WorkoutStatsClient(@Value("${app-service.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .build()
        );
    }

    public ResponseEntity<Object> getWorkouts() {
        return get("");
    }

}
