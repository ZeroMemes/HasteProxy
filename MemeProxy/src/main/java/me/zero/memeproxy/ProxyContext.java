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

import io.netty.channel.ServerChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import me.zero.memeproxy.channel.protocol.TcpChannelInitializer;
import me.zero.memeproxy.channel.protocol.UdpChannelInitializer;
import me.zero.memeproxy.interfaces.ChannelInitializerProvider;
import me.zero.memeproxy.interfaces.Interceptor;
import udpserversocketchannel.channel.UdpServerChannel;

import java.net.SocketAddress;
import java.util.function.Supplier;

/**
 * @author Brady
 * @since 8/14/2018
 */
public class ProxyContext {

    public final Type type;
    public final SocketAddress destination;
    public final Supplier<Interceptor> interceptorProvider;

    ProxyContext(Type type, SocketAddress destination, Supplier<Interceptor> interceptorProvider) {
        this.type = type;
        this.destination = destination;
        this.interceptorProvider = interceptorProvider;
    }

    public enum Type {

        TCP(
                NioServerSocketChannel.class,
                TcpChannelInitializer::new
        ),

        UDP(
                UdpServerChannel.class,
                UdpChannelInitializer::new
        );

        public final Class<? extends ServerChannel> serverChannelClass;
        public final ChannelInitializerProvider provider;

        Type(Class<? extends ServerChannel> serverChannelClass, ChannelInitializerProvider provider) {
            this.serverChannelClass = serverChannelClass;
            this.provider = provider;
        }
    }
}
