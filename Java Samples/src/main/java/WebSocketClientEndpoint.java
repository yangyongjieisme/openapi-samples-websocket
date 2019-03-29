import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;


public class WebSocketClientEndpoint extends Endpoint {

    Session userSession = null;
    private URI endpointURI;
    private String token;

    public WebSocketClientEndpoint(String endpointUrl, String contextId, String token) throws URISyntaxException {
        this.token = token;
        endpointURI = new URI(String.format("%s?contextId=%s", endpointUrl, contextId));
    }

    public void Connect() throws IOException, DeploymentException {
         //Create the ClientEndpointBuilder
        ClientEndpointConfig.Builder configBuilder = ClientEndpointConfig.Builder.create();
        //Add the Authorization header to the request, and make sure you  use the BEARER token scheme.
        configBuilder.configurator(new ClientEndpointConfig.Configurator() {
            @Override
            public void beforeRequest(Map<String, List<String>> headers) {
                headers.put("Authorization", Arrays.asList("BEARER " + token));
            }
        });
        ClientEndpointConfig clientConfig = configBuilder.build();

        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        container.connectToServer(this, clientConfig, endpointURI);
    }

    @Override
    public void onOpen(Session userSession, EndpointConfig config) {
        System.out.println("opening websocket");
        this.userSession = userSession;
        //Add a Saxo Bank message handler to the user session.
        userSession.addMessageHandler(new SaxoBankWebSocketMessageHandler());
    }

    @Override
    public void onClose(Session userSession, CloseReason reason) {
        System.out.println("closing websocket");
        this.userSession = null;
    }

    @Override
    public void onError(Session session, Throwable error) {
        System.out.println("Error communicating with peer " + session.getId() + ". Detail: "+ error.getMessage());
    }
}

