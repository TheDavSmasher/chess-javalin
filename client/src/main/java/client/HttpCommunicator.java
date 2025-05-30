package client;

import model.response.ErrorResponse;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static model.Serializer.deserialize;

public class HttpCommunicator {
    private static final HttpClient client = HttpClient.newHttpClient();

    public static <T> T doPost(String urlString, String body, String authToken, Class<T> responseClass) throws IOException {
        return doServerMethod(urlString, getRequestBuilder().POST(bodyPublisher(body)), authToken, responseClass);
    }

    public static void doDelete(String urlString, String authToken) throws IOException {
        doServerMethod(urlString, getRequestBuilder().DELETE(), authToken, null);
    }

    public static void doPut(String urlString, String body, String authToken) throws IOException {
        doServerMethod(urlString, getRequestBuilder().PUT(bodyPublisher(body)), authToken, null);
    }

    public static <T> T doGet(String urlString, String authToken, Class<T> responseClass) throws IOException {
        return doServerMethod(urlString, getRequestBuilder().GET(), authToken, responseClass);
    }

    private static <T> T doServerMethod(String url, HttpRequest.Builder builder, String authToken, Class<T> responseClass) throws IOException {
        builder.uri(URI.create(url));

        if (authToken != null) {
            builder.header("Authorization", authToken);
        }

        HttpResponse<String> response;
        try {
            response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException e) {
            throw new IOException(e.getMessage());
        }

        if (response.statusCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException(deserialize(response.body(), ErrorResponse.class).message());
        }
        return responseClass != null ? deserialize(response.body(), responseClass) : null;
    }

    private static HttpRequest.Builder getRequestBuilder() {
        return HttpRequest.newBuilder().timeout(Duration.ofMillis(5000));
    }

    private static HttpRequest.BodyPublisher bodyPublisher(String body) {
        return HttpRequest.BodyPublishers.ofString(body);
    }
}
