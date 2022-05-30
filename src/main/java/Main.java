import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.*;
import java.util.Arrays;

public class Main {

    public static final String NASA_URL =
            "https://api.nasa.gov/planetary/apod?api_key=4eyYqqi0qy43fRHaP4Ob5uO8DJA4B3BTM6yz4eK0";
    public static final ObjectMapper mapper = new ObjectMapper();

    public static final String PATH = "D://Alexey/JavaCore/HTTP/image/";

    public static void main(String[] args) {
        try {
            CloseableHttpClient httpClient = HttpClientBuilder.create()
                    .setDefaultRequestConfig(RequestConfig.custom()
                            .setConnectTimeout(5000)
                            .setSocketTimeout(30000)
                            .setRedirectsEnabled(false)
                            .build())
                    .build();

//            HttpGet request = new HttpGet(NASA_URL);
//            request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());

            CloseableHttpResponse response = getResponse(httpClient, NASA_URL);
            Arrays.stream(response.getAllHeaders()).forEach(System.out::println);
            Nasa nasa = mapper.readValue(response.getEntity().getContent(),
                    new TypeReference<>() {
                    }
            );
            String nasaUrl = nasa.getMediaType().equals("image") ?
                    nasa.getUrl() :
                    "http://img.youtube.com/vi/" + nasa.getUrl().split("watch\\?v=")[1] + "/maxresdefault.jpg";

            CloseableHttpResponse nasaResponse = getResponse(httpClient, nasaUrl);

            String filePath = PATH + new File(nasaUrl).getName();
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
            nasaResponse.getEntity().writeTo(bos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static CloseableHttpResponse getResponse(CloseableHttpClient httpClient, String url) throws IOException {
        HttpGet request = new HttpGet(url);
        return httpClient.execute(request);
    }
}
