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

import me.zero.memeproxy.socket.ISocket;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Queue;

/**
 * @author Brady
 * @since 8/14/2018
 */
public class UDPSocket implements ISocket {

    private Queue<ByteBuffer> receiveQueue = new ArrayDeque<>();
    private SocketAddress dest;
    private DatagramSocket socket;

    UDPSocket(DatagramSocket socket, SocketAddress dest) {
        this.socket = socket;
        this.dest = dest;
    }

    @Override
    public ByteBuffer receive(int bufferSize) {
        return receiveQueue.poll();
    }

    @Override
    public void dispatch(ByteBuffer buffer) throws IOException {
        byte[] raw = buffer.array();
        DatagramPacket packet = new DatagramPacket(raw, raw.length, this.dest);
        this.socket.send(packet);
    }

    @Override
    public void close() throws IOException {
        this.socket.close();
    }

    final void addToReceiveQueue(ByteBuffer buffer) {
        this.receiveQueue.add(buffer);
    }
}
