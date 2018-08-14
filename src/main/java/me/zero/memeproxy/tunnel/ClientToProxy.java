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

package me.zero.memeproxy.tunnel;

import me.zero.memeproxy.Connection;
import me.zero.memeproxy.interfaces.ByteTransformer;
import me.zero.memeproxy.Utils;

import java.io.*;
import java.nio.ByteBuffer;

/**
 * @author Brady
 * @since 8/13/2018
 */
public class ClientToProxy extends ProxyTunnel {

    private static int threadNum = 0;
    private ByteTransformer encrypt;

    public ClientToProxy(Connection connection, int buffer) {
        super("ClientToProxy-Thread-" + ++threadNum, connection, buffer);
    }

    @Override
    public void run() {
        super.run();
        try {
            while (!this.isInterrupted()) {
                ByteBuffer buf = this.connection.getClient().receive(this.buffer);
                if (buf != null) {

                    if (this.interceptor != null) {
                        if (!this.interceptor.clientToServer(buf, this.connection))
                            continue;

                        ByteBuffer toSend;
                        while ((toSend = this.interceptor.getServerSendQueue().poll()) != null) {
                            this.connection.getServer().dispatch(toSend);
                        }
                    }

                    if (this.encrypt != null)
                        buf = this.encrypt.apply(buf);

                    this.connection.getServer().dispatch(buf);
                }
                Utils.sleep();
            }
        } catch (IOException e) {
            System.out.println("Client connection closed");
            e.printStackTrace();
        }

        try {
            this.connection.getServer().close();
        } catch (IOException ignored) {}
    }

    public final void setEncrypt(ByteTransformer encrypt) {
        this.encrypt = encrypt;
    }
}