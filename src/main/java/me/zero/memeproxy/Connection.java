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

import me.zero.memeproxy.socket.ISocket;
import me.zero.memeproxy.tunnel.ClientToProxy;
import me.zero.memeproxy.tunnel.ProxyToServer;
import me.zero.memeproxy.interfaces.ByteTransformer;
import me.zero.memeproxy.interfaces.Interceptor;

/**
 * @author Brady
 * @since 8/14/2018
 */
public final class Connection {

    /**
     * The socket established with the client
     */
    private final ISocket client;

    /**
     * The socket established with the server
     */
    private final ISocket server;

    /**
     * Tunnel between the client and the proxy
     */
    private final ClientToProxy c2p;

    /**
     * Tunnel between the proxy and the server
     */
    private final ProxyToServer p2s;

    public Connection(ISocket client, ISocket server) {
        this.client = client;
        this.server = server;

        this.c2p = new ClientToProxy(this, 4096);
        this.p2s = new ProxyToServer(this, 4096);
    }

    final void setInterceptor(Interceptor interceptor) {
        this.c2p.setInterceptor(interceptor);
        this.p2s.setInterceptor(interceptor);
    }

    public final void start() {
        this.c2p.start();
        this.p2s.start();
    }

    public final void createEncyrption(ByteTransformer encrypt, ByteTransformer decrypt) {
        this.c2p.setEncrypt(encrypt);
        this.p2s.setDecrypt(decrypt);
    }

    public final ISocket getClient() {
        return this.client;
    }

    public final ISocket getServer() {
        return this.server;
    }
}
