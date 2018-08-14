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

package me.zero.memeproxy.connection;

import me.zero.memeproxy.Connection;
import me.zero.memeproxy.interfaces.ByteTransformer;
import me.zero.memeproxy.Utils;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author Brady
 * @since 8/13/2018
 */
public class ProxyToServer extends ProxyConnection {

    private static int threadNum = 0;
    private ByteTransformer decrypt;

    public ProxyToServer(Connection connection, int buffer) {
        super("ProxyToServer-Thread-" + ++threadNum, connection, buffer);
    }

    @Override
    public void run() {
        super.run();
        try {
            while (true) {
                byte[] buf = new byte[this.buffer];
                int read = this.connection.getServer().getInputStream().read(buf);
                if (read > 0) {

                    ByteBuffer bytes = ByteBuffer.allocate(read);
                    bytes.put(buf, 0, read);

                    if (decrypt != null)
                        bytes = decrypt.apply(bytes);

                    if (!this.interceptor.serverToClient(bytes, this.connection))
                        return;

                    ByteBuffer toSend;
                    while ((toSend = this.interceptor.getClientSendQueue().poll()) != null) {
                        this.connection.getClient().getOutputStream().write(toSend.array());
                        this.connection.getClient().getOutputStream().flush();
                    }

                    this.connection.getClient().getOutputStream().write(bytes.array());
                    this.connection.getClient().getOutputStream().flush();
                }
                Utils.sleep();
            }
        } catch (IOException e) {
            System.out.println("Server connection closed");
        }

        try {
            this.connection.getClient().close();
        } catch (IOException ignored) {}
    }

    public final void setDecrypt(ByteTransformer decrypt) {
        this.decrypt = decrypt;
    }
}
