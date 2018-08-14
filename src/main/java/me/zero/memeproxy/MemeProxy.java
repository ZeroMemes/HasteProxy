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

import me.zero.memeproxy.interfaces.Interceptor;
import me.zero.memeproxy.interfaces.ServerSocketProvider;
import me.zero.memeproxy.socket.IServerSocket;
import me.zero.memeproxy.socket.tcp.TCPServerSocket;
import me.zero.memeproxy.socket.udp.UDPServerSocket;

import java.io.IOException;
import java.util.Objects;

/**
 * @author Brady
 * @since 8/13/2018
 */
public final class MemeProxy extends Thread {

    private static int threadNum = 0;

    private final Interceptor interceptor;
    private IServerSocket listener;

    /**
     * Creates a new Proxy server.
     *
     * @param srcHost The local address to host on
     * @param srcPort The local port to host on
     * @param destHost The destination host for clients
     * @param destPort The destination port for clients
     * @param interceptor The packet interceptor being used
     * @param type The proxy protocol type
     */
    public MemeProxy(String srcHost, int srcPort, String destHost, int destPort, Interceptor interceptor, ProxyType type) {
        super("Proxy-Thread-" + ++threadNum);
        System.out.println("Creating " + type.toString() + " Proxy for " + destHost + ":" + destPort + " on " + srcHost + ":" + srcPort);
        this.interceptor = interceptor;
        this.listener = type.newServerSocket(srcHost, srcPort, destHost, destPort);
        Objects.requireNonNull(this.listener);
    }

    @Override
    public void run() {
        super.run();
        this.listener.waitForConnection(connection ->
                // I shouldn't have to cast this but for some reason it's making me do it
                ((Connection) connection).setInterceptor(this.interceptor));
    }

    public enum ProxyType {
        TCP(TCPServerSocket::new),

        /**
         * Does not work ATM
         */
        UDP(UDPServerSocket::new);

        private final ServerSocketProvider provider;

        ProxyType(ServerSocketProvider provider) {
            this.provider = provider;
        }

        public final IServerSocket<?> newServerSocket(String srcHost, int srcPort, String destHost, int destPort) {
            try {
                return provider.provide(srcHost, srcPort, destHost, destPort);
            } catch (IOException e) {
                System.out.println("Failed to create server socket");
                e.printStackTrace();
                return null;
            }
        }
    }
}
