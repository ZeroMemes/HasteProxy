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

package me.zero.memeproxy.channel.protocol;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import me.zero.memeproxy.ProxyContext;
import me.zero.memeproxy.channel.handler.ClientToProxyHandler;

/**
 * @author Brady
 * @since 8/14/2018
 */
public class TcpChannelInitializer extends ChannelInitializer<SocketChannel> {

    private ProxyContext proxy;

    public TcpChannelInitializer(ProxyContext proxy) {
        this.proxy = proxy;
    }

    @Override
    protected final void initChannel(SocketChannel ch) {
        ch.pipeline().addLast(new ClientToProxyHandler(this.proxy));
    }
}
