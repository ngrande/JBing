package Bing.Translation;

import org.Json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Class to request the token for the Bing API
 * Created by ngrande on 4/10/15.
 */
public class TokenRequest {
    public TokenRequest(String clientID, String clientSecret) {
        final String encoding = "UTF-8";
        try {
            this.clientID = URLEncoder.encode(clientID, encoding);
            this.clientSecret = URLEncoder.encode(clientSecret, encoding);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    private String clientID;
    private String clientSecret;

    private JSONObject tokenJsonObject;
    private TokenObject tokenObject;

    private String getJsonMember(String name) throws Exception {
        if (tokenJsonObject != null) {
            return tokenJsonObject.get(name).toString();
        } else {
            throw new Exception("Call 'requestToken' first.");
        }
    }

    public synchronized TokenObject requestTokenObject() {
        String requestDetails = String.format("grant_type=client_credentials&client_id=%s&client_secret=%s&scope=http://api.microsofttranslator.com", clientID, clientSecret);

        tokenJsonObject = null;
        String translatorAccessURI = "https://datamarket.accesscontrol.windows.net/v2/OAuth2-13";
        tokenJsonObject = executePost(translatorAccessURI, requestDetails);
        if (tokenJsonObject != null) {
            try {
                return new TokenObject(getJsonMember("access_token"), getJsonMember("token_type"), getJsonMember("scope"), getJsonMember("expires_in"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private synchronized JSONObject executePost(String targetURL, String urlParameters) {
        URL url;
        HttpURLConnection connection = null;
        try {
            //Create connection
            url = new URL(targetURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Length", Integer.toString(urlParameters.getBytes().length));
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            //Send request
            DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
            dataOutputStream.writeBytes(urlParameters);
            dataOutputStream.flush();
            dataOutputStream.close();

            //Get Response
            InputStream errorStream = connection.getErrorStream();
            if (errorStream == null) {
                JSONObject jsonObject;
                try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String inputStr;
                    StringBuilder responseStrBuilder = new StringBuilder();

                    while ((inputStr = bufferedReader.readLine()) != null)
                        responseStrBuilder.append(inputStr);
                    jsonObject = new JSONObject(responseStrBuilder.toString());
                }

                connection.disconnect();
                return jsonObject;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }
}
