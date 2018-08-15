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

package me.zero.hasteproxy.channel.handler;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import me.zero.hasteproxy.ProxyContext;

/**
 * Based on Netty's HexDumpProxyFrontendHandler example class
 *
 * @author Brady
 * @since 8/14/2018
 */
public class ClientToProxyHandler extends Handler {

    /**
     * The channel from the client to the server
     */
    private Channel outboundChannel;

    public ClientToProxyHandler(ProxyContext proxy) {
        super(proxy, proxy.interceptorProvider.get());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Channel inboundChannel = ctx.channel();

        Bootstrap b = new Bootstrap();
        b.group(inboundChannel.eventLoop())
                .channel(inboundChannel.getClass())
                .handler(new ProxyToServerHandler(this.proxy, inboundChannel, this.interceptor))
                .option(ChannelOption.AUTO_READ, false);

        ChannelFuture f = b.connect(this.proxy.destination);
        this.outboundChannel = f.channel();
        f.addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                inboundChannel.read();
            } else {
                inboundChannel.close();
            }
        });
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        closeOnFlush(this.outboundChannel);
    }

    @Override
    protected final void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
        if (this.outboundChannel.isActive()) {

            while (!this.interceptor.getServerSendQueue().isEmpty())
                this.outboundChannel.writeAndFlush(this.interceptor.getServerSendQueue().poll());

            if (this.interceptor.clientToServer(msg.copy())) {
                this.outboundChannel.writeAndFlush(msg.retain()).addListener((ChannelFutureListener) future -> {
                    if (future.isSuccess()) {
                        ctx.channel().read();
                    } else {
                        future.channel().close();
                    }
                });
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        closeOnFlush(ctx.channel());
    }
}
