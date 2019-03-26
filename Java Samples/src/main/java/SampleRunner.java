import javax.websocket.DeploymentException;
import java.io.IOException;
import java.net.URISyntaxException;

public class SampleRunner {
    /// Files included in this sample
    /// SampleRunner.java
    /// This class sets up the sample and is also responsible for running the sample.
    ///
    /// PriceSubscriptionClient.java
    /// This class sets up the price subscription that will send price updates to the Web Socket connection.
    ///
    /// WebSocketClientEndpoint.java
    /// This class sets up the Web Socket connection.
    ///
    /// SaxoBankWebSocketMessageHandler.java
    /// This class is responsible for handling the messages sent over the Web Socket connection.
    /// It handles the parsing of the message header and message payload.
    ///
    ///
    /// This sample shows how to connect to the Streaming Server and set up a subscription to start receiving messages.
    ///
    /// The connection flow is like this.
    ///
    /// 1. First open a streaming WebSocket connection.
    ///    The WebSocket connection is identified by a ContextId, that has to be used when later setting up subscriptions.
    ///    You can start creating the subscription (step 2) in a parallel task. There is a few seconds of buffering
    ///	   so you won't lose messages even if the subscription starts pushing messages before a WebSocket
    ///    connection is established.
    ///
    /// 2. Create a subscription
    ///    Subscriptions define the type of data you want to receive continuous updates on.
    ///    Each subscription is identified by a ContextId and a ReferenceId.
    ///    The ContextId tells our servers what streaming connection you want these messages to be sent to.
    ///    So you need to use the same ContextId you used when opening the streaming connection.
    ///    The ReferenceId uniquely identifies the subscription on the streaming connection.
    ///    It is possible to set up multiple subscriptions on a single WebSocket connection. When you receive them
    ///    you can distinguish what subscription a message belongs to by inspecting the ReferenceId in the message header.
    ///
    /// 3. Receive and parse messages
    ///    Messages are sent as a binary byte stream. The first part of the message is the message envelope or the headers.
    ///    These header fields have a set format, and should always be parsed like this.
    ///    Be aware that the byte order of the numeric fields in the header is little endian.
    ///
    ///    ------------------------------------------------------------------------------------------
    ///    | Field                | Bytes | Type   | Note                                            |
    ///    | ----------------------------------------------------------------------------------------|
    ///    | Message Id	          | 8     | Int64  | 64-bit little-endian unsigned integer.           |
    ///    | Envelope Version     | 2     | Int16  | Always 0.                                       |
    ///    | Reference Id Size    | 1     | Int8   | 8-bit little-endian unsigned integer.           |
    ///    | Reference Id         | n     | Ascii  | n = Reference Id Size. String is ASCII encoded. |
    ///    | Payload Format       | 1     | Int8   | 0 = Json. 8-bit unsigned integer.               |
    ///    | Payload Size         | 4     | Int32  | 32-bit little-endian unsigned integer.          |
    ///    | Payload              | n     | Byte[] | n = Payload Size. Json is UTF8 encoded.         |
    ///    ------------------------------------------------------------------------------------------
    ///    (*) Timestamp is the number of 100-nanosecond intervals that have elapsed
    ///     since 12:00:00 midnight, January 1, 0001 (0:00:00 UTC on January 1, 0001, in the Gregorian calendar).
    ///
    /// 4. Response codes to be aware of.
    ///	   409 Conflict. This means that you have tried to reuse a ContextId. This is not possible. So please create a new ContextId and try again.
    ///    429 Too many requests. This means that you have been throttled. Please wait a little while before trying again.

	public static void main(String[] args) {
		try {
            //A valid OAuth2 _token.
            String token = "eyJhbGciOiJFUzI1NiIsIng1dCI6IjQ4QjM4MEVBNDU3RjJCRDQwNjgyNzlBODA2RjY1RTc4MEZGNDg3NzYifQ.eyJvYWEiOiI3Nzc3NyIsImlzcyI6Im9hIiwiYWlkIjoiMTE4IiwidWlkIjoiTE9oNlpvSmdmYmJ6YTAxSzgyaEplZz09IiwiY2lkIjoiTE9oNlpvSmdmYmJ6YTAxSzgyaEplZz09IiwiaXNhIjoiVHJ1ZSIsInRpZCI6IjIwMDIiLCJzaWQiOiJkZjU2YzJmYzM1ZTI0YjZjODc5YzVhZjMxOTY2MGE0ZSIsImRnaSI6IjgyIiwiZXhwIjoiMTUyOTA1MjA5OCJ9.1SMV_bmjaO3jWUGA_E9iIJ34bK_itiRGtn-QRTibkPJBVG2rfn-HdvvCnG9F4kIbBzzATcJ9fMm2C8HT_yuVvQ";

            //Url for streaming server.
            String webSocketConnectionUrl = "wss://ab.orange.saxobank.com/openapi/streamingws/connect";

            //Url for creating price subscription.
            String priceSubscriptionUrl = "https://ab.orange.saxobank.com/openapi/trade/v1/prices/subscriptions";

            //A string provided by the client to correlate the stream and the subscription. Multiple subscriptions can use the same contextId.
            String contextId = "ctx101";

            //A unique string provided by the client to identify a certain subscription in the stream.
            String referenceId = "refAbc";

            //Open the Web Socket connection
            final WebSocketClientEndpoint clientEndPoint = new WebSocketClientEndpoint(webSocketConnectionUrl, contextId, token);
            clientEndPoint.Connect();

            //Subscribe to price subscriptions
            final PriceSubscriptionClient priceSubscriptionClient = new PriceSubscriptionClient(priceSubscriptionUrl);
            String subscriptionSnapshotResponse = priceSubscriptionClient.CreateSubscription(token, contextId, referenceId);

            //Wait 5 seconds for messages from Web Socket and then stop.
            Thread.sleep(50000);

        } catch (InterruptedException ex) {
            System.err.println("InterruptedException exception: " + ex.getMessage());
        } catch (URISyntaxException ex) {
            System.err.println("URISyntaxException exception: " + ex.getMessage());
        } catch (IOException ex) {
            System.err.println("Exception when setting up subscription: " + ex.getMessage());
        } catch (DeploymentException ex) {
            System.err.println("Exception when setting up subscription: " + ex.getMessage());
        }
    }
}
