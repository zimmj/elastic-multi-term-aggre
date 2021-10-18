package com.joel.zimmerli.elastic.low;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class CallService {

    private static final String PROTOCOL = "HTTP";
    private static final String INDEX_NAME = "my_index";

    private static final String USER_NAME = "elastic";
    private static final String PASSWORD = "pass";

    private static final String ELASTIC_HOST = "localhost:9200, elastic:9201";

    private final RestClient restClient;

    public CallService() {
        String[] elasticHostArray = ELASTIC_HOST.split(",");
        List<HttpHost> httpHosts = new ArrayList<>();
        for (String host: elasticHostArray) {
            String[] hostAndPort = host.trim().split(":");
            httpHosts.add(
                    new HttpHost(hostAndPort[0], Integer.parseInt(hostAndPort[1]), PROTOCOL)
            );
        }

        restClient = RestClient.builder(
                httpHosts.toArray(new HttpHost[0])
        ).setDefaultHeaders(
                new Header[] {
                        new BasicHeader("Authorization", getBasicAuth())
                }
        ).build();
    }

    private static String getBasicAuth() {
        String auth = USER_NAME + ":" + PASSWORD;
        return "Basic " + new String(Base64.encodeBase64(auth.getBytes(StandardCharsets.UTF_8)));
    }

    public String makeCall(String jsonSearchCall) throws IOException {
        var request = new Request(
                "GET",
                "/" + INDEX_NAME + "/_search"
        );
        request.setJsonEntity(jsonSearchCall);
        return getResult(restClient.performRequest(request));
    }

    private String getResult(Response response) throws IOException {
        if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
            return EntityUtils.toString(response.getEntity());
        }

        String errorMessage = MessageFormat.format(
                "Could Not Perform search code: {0}. Error Message is: \n {1}",
                response.getStatusLine().getStatusCode(),
                response.getStatusLine().getReasonPhrase()
        );
                throw new IllegalArgumentException(errorMessage);
    }
}
