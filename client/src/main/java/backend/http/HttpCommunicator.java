package backend.http;

import model.response.ErrorResponse;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static utils.Catcher.*;
import static utils.Serializer.*;

public record HttpCommunicator(String serverUrl) {
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final HttpRequest.Builder standardRequest = HttpRequest.newBuilder().timeout(Duration.ofMillis(5000));

    // region HTTP Methods
    public <T> T doPost(String path, Object body, String authToken, Class<T> responseClass) throws IOException {
        return makeRequest(HttpMethod.POST, path, body, authToken, responseClass);
    }

    public <T> T doPost(String path, Object body, Class<T> responseClass) throws IOException {
        return doPost(path, body, null, responseClass);
    }

    public void doDelete(String path, String authToken) throws IOException {
        makeRequest(HttpMethod.DELETE, path, null, authToken, null);
    }

    public void doDelete(String path) throws IOException {
        doDelete(path, null);
    }

    public void doPut(String path, Object body, String authToken) throws IOException {
        makeRequest(HttpMethod.PUT, path, body, authToken, null);
    }

    public <T> T doGet(String path, String authToken, Class<T> responseClass) throws IOException {
        return makeRequest(HttpMethod.GET, path, null, authToken, responseClass);
    }
    // endregion

    private <T> T makeRequest(HttpMethod method, String path, Object body, String authToken, Class<T> responseClass)
            throws IOException {
        return sendRequest(createRequest(method, path, body, authToken), responseClass);
    }

    private HttpRequest createRequest(HttpMethod method, String path, Object body, String authToken) {
        HttpRequest.Builder builder = standardRequest.copy()
                .uri(URI.create(serverUrl + path))
                .method(method.name(), bodyPublisher(body));
        if (authToken != null) {
            builder.header("Authorization", authToken);
        }
        return builder.build();
    }

    private static HttpRequest.BodyPublisher bodyPublisher(Object body) {
        if (body == null) {
            return HttpRequest.BodyPublishers.noBody();
        }
        return HttpRequest.BodyPublishers.ofString(serialize(body));
    }

    private static <T> T sendRequest(HttpRequest request, Class<T> responseClass) throws IOException {
        HttpResponse<String> response = tryCatchRethrow(
                () -> client.send(request, HttpResponse.BodyHandlers.ofString()),
                InterruptedException.class, IOException.class);

        if (response.statusCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException(deserialize(response.body(), ErrorResponse.class).message());
        }
        if (responseClass == null) {
            return null;
        }
        return deserialize(response.body(), responseClass);
    }
}
