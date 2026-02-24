package ru.netology.moneytransfer.integration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers(disabledWithoutDocker = true)
class MoneyTransferContainerIT {

    @Container
    static GenericContainer<?> app = new GenericContainer<>(
            new ImageFromDockerfile()
                    .withFileFromPath(".", java.nio.file.Paths.get("."))
    ).withExposedPorts(5500)
            .withStartupAttempts(2);

    @Test
    @EnabledIfSystemProperty(named = "runDockerIT", matches = "true")
    void shouldReturnBadRequestForInvalidTransferPayload() throws Exception {
        String endpoint = "http://" + app.getHost() + ":" + app.getMappedPort(5500) + "/transfer";
        URL url = new URL(endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        byte[] payload = "{}".getBytes(StandardCharsets.UTF_8);
        connection.getOutputStream().write(payload);
        int statusCode = connection.getResponseCode();
        connection.disconnect();

        assertEquals(400, statusCode);
    }
}
