package me.logincraftlaunch.api;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@UtilityClass
public class Api {

    private static final ScheduledExecutorService EXECUTOR = Executors.newScheduledThreadPool(4);

    public ScheduledExecutorService executor() {
        return EXECUTOR;
    }

    private static final ObjectMapper MAPPER = new ObjectMapper(new JsonFactory());

    public ObjectMapper mapper() {
        return MAPPER;
    }
}
