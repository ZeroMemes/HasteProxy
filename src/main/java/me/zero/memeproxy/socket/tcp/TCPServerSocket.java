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

package me.zero.memeproxy.socket.tcp;

import me.zero.memeproxy.Connection;
import me.zero.memeproxy.Utils;
import me.zero.memeproxy.socket.IServerSocket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;

/**
 * @author Brady
 * @since 8/14/2018
 */
public class TCPServerSocket implements IServerSocket<TCPSocket> {

    private final ServerSocket listener;
    private final String destHost;
    private final int destPort;

    public TCPServerSocket(String srcHost, int srcPort, String destHost, int destPort) throws IOException {
        this.listener = new ServerSocket(srcPort, 50, InetAddress.getByName(srcHost));
        this.destHost = destHost;
        this.destPort = destPort;
    }

    @Override
    public final void waitForConnection(Consumer<Connection> onConnection) {
        while (true) {
            Connection connection;

            System.out.println("Waiting for connection");

            try {
                Socket client = this.listener.accept();
                client.setSoTimeout(20_000);
                Socket server = new Socket(destHost, destPort);
                server.setSoTimeout(20_000);
                System.out.println("Established Connection");

                connection = new Connection(new TCPSocket(client), new TCPSocket(server));
                onConnection.accept(connection);
                System.out.println("Setup Interceptor");
            } catch (Exception e) {
                System.out.println("Failed to establish connection");
                e.printStackTrace();
                continue;
            }

            System.out.println("Starting up Proxy connection");
            connection.start();

            Utils.sleep();
        }
    }
}
