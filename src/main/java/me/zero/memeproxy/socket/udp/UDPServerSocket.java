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

package me.zero.memeproxy.socket.udp;

import me.zero.memeproxy.Connection;
import me.zero.memeproxy.Utils;
import me.zero.memeproxy.socket.IServerSocket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Brady
 * @since 8/14/2018
 */
public class UDPServerSocket implements IServerSocket<UDPSocket> {

    private final DatagramSocket listener;
    private final DatagramSocket dispatch;
    private final SocketAddress dest;
    private Connection connection;

    public UDPServerSocket(String srcHost, int srcPort, String destHost, int destPort) throws IOException {
        this.listener = new DatagramSocket(new InetSocketAddress(srcHost, srcPort));
        this.dest = new InetSocketAddress(destHost, destPort);
        this.dispatch = new DatagramSocket();
    }

    @Override
    public final void waitForConnection(Consumer<Connection> onConnection) {
        while (true) {
            try {
                byte[] buffer = new byte[4096];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                this.listener.receive(packet);
                ByteBuffer buf = ByteBuffer.wrap(packet.getData());

                if (packet.getSocketAddress().equals(dest)) {
                    ((UDPSocket) this.connection.getClient()).addToReceiveQueue(buf);
                } else {
                    if (this.connection == null) {
                        UDPSocket client = new UDPSocket(dispatch, packet.getSocketAddress());
                        UDPSocket server = new UDPSocket(dispatch, dest);
                        this.connection = new Connection(client, server);
                        onConnection.accept(this.connection);
                        this.connection.start();
                    }

                    ((UDPSocket) this.connection.getServer()).addToReceiveQueue(buf);
                }
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            Utils.sleep();
        }
    }
}
