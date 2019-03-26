import javax.websocket.MessageHandler;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class SaxoBankWebSocketMessageHandler implements MessageHandler.Whole<byte[]> {
    @Override
    public void onMessage(byte[] message) {
        parseMessage(message);
        System.out.println("Got binary message - " + message.length);
    }

    private void parseMessage(byte[] message)
    {
        int index = 0;
        ByteBuffer bb = ByteBuffer.wrap(message);
        //Important to set the byte order to little endian. Otherwise you are going to get strange numbers.
        bb.order(ByteOrder.LITTLE_ENDIAN);

        long messageId = bb.getLong(index);
        index += 8;

        short messageVersion = bb.getShort(index);
        index += 2;

        int referenceIdLength = bb.get(index);
        index += 1;

        byte[] partOfByteBuffer = new byte[referenceIdLength];
        System.arraycopy(bb.array(), index, partOfByteBuffer, 0, partOfByteBuffer.length);
        String referenceId = new String(partOfByteBuffer);

        index += referenceIdLength;

        int payloadFormat = bb.get(index);
        index += 1;

        int payloadSize = bb.getInt(index);
        index += 4;

        byte[] payloadByteBuffer = new byte[payloadSize];
        System.arraycopy(bb.array(), index, payloadByteBuffer, 0, payloadByteBuffer.length);
        String payload = new String(payloadByteBuffer);

        index += payloadSize;



        System.out.println("------==== New Message ====------");
        System.out.println("messageId: " + messageId);
        System.out.println("messageVersion: " + messageVersion);
        System.out.println("referenceIdLength: " + referenceIdLength);
        System.out.println("referenceId: " + referenceId);
        System.out.println("payloadFormat: " + (payloadFormat == 0 ? "Json" : "ProtoBuf") );
        System.out.println("payloadSize: " + payloadSize);
        System.out.println("payload: " + payload);
        System.out.println("bufferSize: " + message.length);
        System.out.println("index: " + index);
    }
}
