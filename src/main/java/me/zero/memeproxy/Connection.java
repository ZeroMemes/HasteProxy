package me.zero.memeproxy;

import me.zero.memeproxy.connection.ClientToProxy;
import me.zero.memeproxy.connection.ProxyToServer;
import me.zero.memeproxy.interfaces.ByteTransformer;
import me.zero.memeproxy.interfaces.Interceptor;

import java.net.Socket;

/**
 * @author Brady
 * @since 8/14/2018
 */
public final class Connection {

    private final Socket client, server;
    private final ClientToProxy c2p;
    private final ProxyToServer p2s;

    Connection(Socket client, Socket server) {
        this.client = client;
        this.server = server;

        c2p = new ClientToProxy(this, 4096);
        p2s = new ProxyToServer(this, 4096);
    }

    final void setInterceptor(Interceptor interceptor) {
        this.c2p.setInterceptor(interceptor);
        this.p2s.setInterceptor(interceptor);
    }

    final void start() {
        this.c2p.start();
        this.p2s.start();
    }

    public final void setEncrypt(ByteTransformer encrypt) {
        this.c2p.setEncrypt(encrypt);
    }

    public final void setDecrypt(ByteTransformer decrypt) {
        this.p2s.setDecrypt(decrypt);
    }

    public final Socket getClient() {
        return this.client;
    }

    public final Socket getServer() {
        return this.server;
    }
}
