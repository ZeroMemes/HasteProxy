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

package me.zero.minecraft.bedrock;

import io.netty.buffer.ByteBuf;
import me.zero.memeproxy.MemeProxy;
import me.zero.memeproxy.ProxyContext;
import me.zero.memeproxy.Utils;
import me.zero.memeproxy.interfaces.Interceptor;

import javax.xml.bind.DatatypeConverter;
import java.util.ArrayDeque;
import java.util.Queue;

/**
 * @author Brady
 * @since 8/14/2018
 */
public class MinecraftBedrockProxy {

    public static void main(String[] args) throws InterruptedException {
        new MemeProxy(args[0], Integer.valueOf(args[1]), args[2], Integer.valueOf(args[3]), () -> new Interceptor() {

            private Queue<ByteBuf> clientSendQueue = new ArrayDeque<>();
            private Queue<ByteBuf> serverSendQueue = new ArrayDeque<>();

            @Override
            public boolean clientToServer(ByteBuf msg) {
                System.out.println("client->server " + DatatypeConverter.printHexBinary(Utils.toByteArraySafe(msg)));
                return true;
            }

            @Override
            public boolean serverToClient(ByteBuf msg) {
                System.out.println("server->client " + DatatypeConverter.printHexBinary(Utils.toByteArraySafe(msg)));
                return true;
            }

            @Override
            public Queue<ByteBuf> getClientSendQueue() {
                return this.clientSendQueue;
            }

            @Override
            public Queue<ByteBuf> getServerSendQueue() {
                return this.serverSendQueue;
            }

        }, ProxyContext.Type.UDP).run();
    }
}
