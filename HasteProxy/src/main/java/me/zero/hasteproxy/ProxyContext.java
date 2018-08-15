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

package me.zero.hasteproxy;

import io.netty.channel.Channel;
import io.netty.channel.ServerChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import me.zero.hasteproxy.channel.initializer.TcpChannelInitializer;
import me.zero.hasteproxy.channel.initializer.UdpChannelInitializer;
import me.zero.hasteproxy.interfaces.ChannelInitializerProvider;
import me.zero.hasteproxy.interfaces.Interceptor;
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
                NioSocketChannel.class,
                TcpChannelInitializer::new
        ),

        UDP(
                UdpServerChannel.class,
                NioDatagramChannel.class,
                UdpChannelInitializer::new
        );

        public final Class<? extends ServerChannel> serverChannelClass;
        public final Class<? extends Channel> channelClass;
        public final ChannelInitializerProvider provider;

        Type(Class<? extends ServerChannel> serverChannelClass, Class<? extends Channel> channelClass, ChannelInitializerProvider provider) {
            this.serverChannelClass = serverChannelClass;
            this.channelClass = channelClass;
            this.provider = provider;
        }
    }
}
