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

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import me.zero.memeproxy.interfaces.Interceptor;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.function.Supplier;

/**
 * @author Brady
 * @since 8/13/2018
 */
public final class MemeProxy {

    private final SocketAddress bindAddress;
    private final ProxyContext context;

    /**
     * Creates a new Proxy server.
     *
     * @param srcHost The local address to host on
     * @param srcPort The local port to host on
     * @param destHost The destination host for clients
     * @param destPort The destination port for clients
     * @param interceptorProvider The packet interceptor being used
     * @param type The proxy protocol type
     */
    public MemeProxy(String srcHost, int srcPort, String destHost, int destPort, Supplier<Interceptor> interceptorProvider, ProxyContext.Type type) {
        System.out.println("Creating " + type.toString() + " Proxy for " + destHost + ":" + destPort + " on " + srcHost + ":" + srcPort);
        this.bindAddress = new InetSocketAddress(srcHost, srcPort);
        this.context = new ProxyContext(type, new InetSocketAddress(destHost, destPort), interceptorProvider);
    }

    public void run() throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ChannelFuture f = new ServerBootstrap().group(bossGroup, workerGroup)
                .channel(this.context.type.serverChannelClass)
                .childHandler(this.context.type.provider.provide(this.context))
                .childOption(ChannelOption.AUTO_READ, false)
                .bind(this.bindAddress)
                .sync();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                f.channel().closeFuture().sync();
            } catch (InterruptedException ignored) {}
        }));
    }
}
