package backend;

import model.response.ErrorResponse;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static utils.Catcher.*;
import static utils.Serializer.deserialize;
import static utils.Serializer.serialize;

public record HttpCommunicator(String serverUrl) {
    private static final HttpClient client = HttpClient.newHttpClient();

    public <T> T doPost(String path, Object body, String authToken, Class<T> responseClass) throws IOException {
        return doServerMethod(path, getRequestBuilder().POST(bodyPublisher(body)), authToken, responseClass);
    }

    public <T> T doPost(String path, Object body, Class<T> responseClass) throws IOException {
        return doPost(path, body, null, responseClass);
    }

    public void doDelete(String path, String authToken) throws IOException {
        doServerMethod(path, getRequestBuilder().DELETE(), authToken, null);
    }

    public void doDelete(String path) throws IOException {
        doDelete(path, null);
    }

    public void doPut(String path, Object body, String authToken) throws IOException {
        doServerMethod(path, getRequestBuilder().PUT(bodyPublisher(body)), authToken, null);
    }

    public <T> T doGet(String path, String authToken, Class<T> responseClass) throws IOException {
        return doServerMethod(path, getRequestBuilder().GET(), authToken, responseClass);
    }

    private <T> T doServerMethod(String path, HttpRequest.Builder builder, String authToken, Class<T> responseClass) throws IOException {
        builder.uri(URI.create(serverUrl + path));

        if (authToken != null) {
            builder.header("Authorization", authToken);
        }

        HttpResponse<String> response = tryCatchRethrow(
                () -> client.send(builder.build(), HttpResponse.BodyHandlers.ofString()),
                InterruptedException.class, IOException.class);

        if (response.statusCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException(deserialize(response.body(), ErrorResponse.class).message());
        }
        return responseClass != null ? deserialize(response.body(), responseClass) : null;
    }

    private static HttpRequest.Builder getRequestBuilder() {
        return HttpRequest.newBuilder().timeout(Duration.ofMillis(5000));
    }

    private static HttpRequest.BodyPublisher bodyPublisher(Object body) {
        return HttpRequest.BodyPublishers.ofString(serialize(body));
    }
}
