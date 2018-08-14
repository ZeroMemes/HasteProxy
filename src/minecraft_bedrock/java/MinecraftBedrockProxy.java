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

import me.zero.memeproxy.Connection;
import me.zero.memeproxy.MemeProxy;
import me.zero.memeproxy.interfaces.Interceptor;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Queue;

/**
 * @author Brady
 * @since 8/14/2018
 */
public class MinecraftBedrockProxy {

    public static void main(String[] args) throws IOException {
        new MemeProxy(args[0], Integer.valueOf(args[1]), args[2], Integer.valueOf(args[3]), new Interceptor() {

            private Queue<ByteBuffer> clientSendQueue = new ArrayDeque<>();
            private Queue<ByteBuffer> serverSendQueue = new ArrayDeque<>();

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
            public Queue<ByteBuffer> getClientSendQueue() {
                return this.clientSendQueue;
            }

            @Override
            public Queue<ByteBuffer> getServerSendQueue() {
                return this.serverSendQueue;
            }

        }, MemeProxy.ProxyType.UDP).start();
    }
}
