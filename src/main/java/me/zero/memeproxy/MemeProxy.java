/*
 * This file is part of MemeProxy.
 *
 * MemeProxy is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MemeProxy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MemeProxy.  If not, see <https://www.gnu.org/licenses/>.
 */

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
