package me.zero.memeproxy.connection;

import me.zero.memeproxy.Connection;
import me.zero.memeproxy.interfaces.ByteTransformer;
import me.zero.memeproxy.Utils;

import java.io.*;
import java.nio.ByteBuffer;

/**
 * @author Brady
 * @since 8/13/2018
 */
public class ClientToProxy extends ProxyConnection {

    private static int threadNum = 0;
    private ByteTransformer encrypt;

    public ClientToProxy(Connection connection, int buffer) {
        super("ClientToProxy-Thread-" + ++threadNum, connection, buffer);
    }

    @Override
    public void run() {
        super.run();
        try {
            while (true) {
                byte[] buf = new byte[this.buffer];
                int read = this.connection.getClient().getInputStream().read(buf);
                if (read > 0) {

                    ByteBuffer bytes = ByteBuffer.allocate(read);
                    bytes.put(buf, 0, read);

                    if (this.interceptor != null) {
                        if (!this.interceptor.clientToServer(bytes, this.connection))
                            return;

                        ByteBuffer toSend;
                        while ((toSend = this.interceptor.getServerSendQueue().poll()) != null) {
                            this.connection.getServer().getOutputStream().write(toSend.array());
                            this.connection.getServer().getOutputStream().flush();
                        }
                    }

                    if (encrypt != null) {
                        bytes = encrypt.apply(bytes);
                    }

                    this.connection.getServer().getOutputStream().write(bytes.array());
                    this.connection.getServer().getOutputStream().flush();
                }
                Utils.sleep();
            }
        } catch (IOException e) {
            System.out.println("Client connection closed");
        }

        try {
            this.connection.getServer().close();
        } catch (IOException ignored) {}
    }

    public final void setEncrypt(ByteTransformer encrypt) {
        this.encrypt = encrypt;
    }
}
