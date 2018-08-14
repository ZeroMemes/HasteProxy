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

import me.zero.memeproxy.socket.ISocket;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * @author Brady
 * @since 8/14/2018
 */
public class TCPSocket implements ISocket {

    private Socket socket;

    TCPSocket(Socket socket) {
        this.socket = socket;
    }

    @Override
    public ByteBuffer receive(int bufferSize) throws IOException {
        byte[] buf = new byte[bufferSize];
        int read = this.socket.getInputStream().read(buf);
        if (read > 0) {
            buf = Arrays.copyOfRange(buf, 0, read);
            return ByteBuffer.wrap(buf);
        }
        return null;
    }

    @Override
    public void dispatch(ByteBuffer buffer) throws IOException {
        this.socket.getOutputStream().write(buffer.array());
        this.socket.getOutputStream().flush();
    }

    @Override
    public void close() throws IOException {
        this.socket.close();
    }
}
