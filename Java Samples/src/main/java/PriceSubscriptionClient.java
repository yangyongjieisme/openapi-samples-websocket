import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

public class PriceSubscriptionClient {

    private String priceSubscriptionUrl;

    public PriceSubscriptionClient(String priceSubscriptionUrl) {

        this.priceSubscriptionUrl = priceSubscriptionUrl;
    }

    public String CreateSubscription(String token, String contextId, String referenceId) throws IOException {

        URL url = new URL(priceSubscriptionUrl);
        URLConnection con = url.openConnection();
        HttpURLConnection http = (HttpURLConnection)con;
        http.setRequestMethod("POST");
        http.setDoOutput(true);

        String requestBody = "{\"Arguments\": {\"Uic\": 15, \"AssetType\": \"FxSpot\"}, \"ContextId\": \"" + contextId + "\", \"ReferenceId\": \"" + referenceId + "\"}";
        byte[] out = requestBody.getBytes(StandardCharsets.UTF_8);
        int length = out.length;

        http.setFixedLengthStreamingMode(length);
        http.addRequestProperty("Authorization", "BEARER " + token);
        http.addRequestProperty("Content-Type", "application/json");
        http.connect();
        try(OutputStream os = http.getOutputStream()) {
            os.write(out);
            os.flush();
        }

        System.out.println(String.format("Price subscription request returned %s %s", http.getResponseCode(), http.getResponseMessage()));
        BufferedReader in = new BufferedReader(new InputStreamReader(http.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }
}
