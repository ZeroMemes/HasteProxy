/*
 * This file is part of HasteProxy.
 *
 * HasteProxy is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * HasteProxy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with HasteProxy.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.zero.minecraft.java;

import io.netty.buffer.ByteBuf;
import me.zero.hasteproxy.Utils;
import me.zero.hasteproxy.interfaces.Interceptor;
import net.minecraft.network.PacketBuffer;

import javax.xml.bind.DatatypeConverter;
import java.util.ArrayDeque;
import java.util.Queue;

/**
 * @author Brady
 * @since 8/14/2018
 */
public final class InterceptorJavaEdition implements Interceptor {

    private Queue<ByteBuf> clientSendQueue = new ArrayDeque<>();
    private Queue<ByteBuf> serverSendQueue = new ArrayDeque<>();

    @Override
    public final boolean clientToServer(ByteBuf msg) {
        PacketBuffer buffer = new PacketBuffer(msg);
        System.out.println("client->server " + DatatypeConverter.printHexBinary(Utils.toByteArraySafe(buffer)));
        return true;
    }

    @Override
    public final boolean serverToClient(ByteBuf msg) {
        PacketBuffer buffer = new PacketBuffer(msg);
        System.out.println("server->client " + DatatypeConverter.printHexBinary(Utils.toByteArraySafe(buffer)));
        return true;
    }

    @Override
    public final Queue<ByteBuf> getClientSendQueue() {
        return this.clientSendQueue;
    }

    @Override
    public final Queue<ByteBuf> getServerSendQueue() {
        return this.serverSendQueue;
    }
}
