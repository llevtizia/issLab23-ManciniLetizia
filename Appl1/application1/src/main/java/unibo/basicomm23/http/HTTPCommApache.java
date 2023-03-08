package unibo.basicomm23.http;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import unibo.basicomm23.utils.CommUtils;

import java.net.URI;

public class HTTPCommApache {

    private final CloseableHttpClient httpclient = HttpClients.createDefault();
    private final JSONParser simpleparser        = new JSONParser();
    private String URL;

    public HTTPCommApache(String url) {
        URL = "http://" +url;
    }

    protected JSONObject requestSynch( String URL, String crilCmd )  {
        JSONObject jsonEndmove = null;
        try {
            StringEntity entity = new StringEntity(crilCmd);
            HttpUriRequest httppost = RequestBuilder.post()
                    .setUri(new URI(URL))
                    .setHeader("Content-Type", "application/json")
                    .setHeader("Accept", "application/json")
                    .setEntity(entity)
                    .build();
            CloseableHttpResponse response = httpclient.execute(httppost);
            String jsonStr = EntityUtils.toString( response.getEntity() );
            jsonEndmove = (JSONObject) simpleparser.parse(jsonStr);
        } catch(Exception e){
            CommUtils.outred("      requestSynch | ERROR:" + e.getMessage());
        }
        return jsonEndmove;
    }

}