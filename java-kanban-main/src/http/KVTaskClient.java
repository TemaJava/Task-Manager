package http;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final String apiToken;
    private final URI url;

    public KVTaskClient(URI url) throws IOException, InterruptedException {
        this.url = url;
        URI uri = URI.create(url + "/register");
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        apiToken = response.body();
    }

    public void put(String key, String json) throws IOException, InterruptedException {
        URI uri = URI.create(url + "/save/" + key + "?API_TOKEN=" + apiToken);
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(uri).POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        //ничего не возвращаем
    }

    //метод вызывается при создании HttpTask сервера
    public String load(String key) throws IOException, InterruptedException {
        URI uri = URI.create(url + "/load/" + key + "?API_TOKEN=" + apiToken);
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
}
