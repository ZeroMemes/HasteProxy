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

package me.zero.hasteproxy.channel.initializer;

import io.netty.channel.ChannelInitializer;
import me.zero.hasteproxy.ProxyContext;
import me.zero.hasteproxy.channel.handler.ClientToProxyHandler;
import udpserversocketchannel.channel.UdpChannel;

/**
 * @author Brady
 * @since 8/14/2018
 */
public class UdpChannelInitializer extends ChannelInitializer<UdpChannel> {

    private ProxyContext proxy;

    public UdpChannelInitializer(ProxyContext proxy) {
        this.proxy = proxy;
    }

    @Override
    protected final void initChannel(UdpChannel ch) {
        ch.pipeline().addLast(new ClientToProxyHandler(this.proxy));
    }
}
