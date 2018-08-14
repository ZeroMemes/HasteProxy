import me.zero.memeproxy.Connection;
import me.zero.memeproxy.interfaces.Interceptor;
import me.zero.memeproxy.MemeProxy;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Queue;

/**
 * @author Brady
 * @since 8/14/2018
 */
public class MinecraftJavaProxy {

    public static void main(String[] args) throws IOException {
        new MemeProxy(args[0], Integer.valueOf(args[1]), args[2], Integer.valueOf(args[3]), new Interceptor() {

            private Queue<byte[]> clientSendQueue = new ArrayDeque<>();
            private Queue<byte[]> serverSendQueue = new ArrayDeque<>();

            @Override
            public boolean clientToServer(ByteBuffer buffer, Connection connection) {
                System.out.println("client->server " + DatatypeConverter.printHexBinary(buffer.array()));
                return true;
            }

            @Override
            public boolean serverToClient(ByteBuffer buffer, Connection connection) {
                System.out.println("server->client " + DatatypeConverter.printHexBinary(buffer.array()));
                return true;
            }

            @Override
            public Queue<byte[]> getClientSendQueue() {
                return this.clientSendQueue;
            }

            @Override
            public Queue<byte[]> getServerSendQueue() {
                return this.serverSendQueue;
            }
        }).start();
    }
}
