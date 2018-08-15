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

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import me.zero.hasteproxy.ProxyContext;
import me.zero.hasteproxy.interfaces.Interceptor;

/**
 * Based on Netty's HexDumpProxyBackendHandler example class
 *
 * @author Brady
 * @since 8/14/2018
 */
public class ProxyToServerHandler extends Handler {

    /**
     * The channel from the server to the client
     */
    private Channel inboundChannel;

    ProxyToServerHandler(ProxyContext proxy, Channel inboundChannel, Interceptor interceptor) {
        super(proxy, interceptor);
        this.inboundChannel = inboundChannel;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.read();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        closeOnFlush(this.inboundChannel);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
        if (this.inboundChannel.isActive()) {

            while (!this.interceptor.getClientSendQueue().isEmpty())
                this.inboundChannel.writeAndFlush(this.interceptor.getClientSendQueue().poll());

            if (this.interceptor.serverToClient(msg.copy())) {
                this.inboundChannel.writeAndFlush(msg.retain()).addListener((ChannelFutureListener) future -> {
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
