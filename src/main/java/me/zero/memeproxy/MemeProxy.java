package me.zero.memeproxy;

import me.zero.memeproxy.interfaces.Interceptor;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Brady
 * @since 8/13/2018
 */
public final class MemeProxy extends Thread {

    private static int threadNum = 0;

    private final Interceptor interceptor;
    private final String destHost;
    private final int destPort;
    private ServerSocket listener;

    /**
     * Creates a new Proxy server.
     *
     * @param srcHost The local address to host on
     * @param srcPort The local port to host on
     * @param destHost The destination host for clients
     * @param destPort The destination port for clients
     * @param interceptor The packet interceptor being used
     * @throws IOException If establishing the server socket fails
     */
    public MemeProxy(String srcHost, int srcPort, String destHost, int destPort, Interceptor interceptor) throws IOException {
        super("Proxy-Thread-" + ++threadNum);
        System.out.println("Creating Proxy for " + destHost + ":" + destPort + " on " + srcHost + ":" + srcPort);
        this.interceptor = interceptor;
        this.destHost = destHost;
        this.destPort = destPort;
        this.listener = new ServerSocket(srcPort, 50, InetAddress.getByName(srcHost));
    }

    @Override
    public void run() {
        super.run();
        while (true) {
            System.out.println("Waiting for connection");

            Connection connection;

            try {
                Socket client = listener.accept();
                client.setSoTimeout(20_000);
                Socket server = new Socket(destHost, destPort);
                server.setSoTimeout(20_000);

                connection = new Connection(client, server);
                connection.setInterceptor(this.interceptor);
            } catch (Exception e) {
                System.out.println("Failed to establish connection");
                e.printStackTrace();
                return;
            }
            System.out.println("Established connection");

            System.out.println("Exchanging sockets between effective client/server proxies");

            connection.start();

            Utils.sleep();
        }
    }
}
