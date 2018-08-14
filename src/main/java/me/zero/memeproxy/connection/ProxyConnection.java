package me.zero.memeproxy.connection;

import me.zero.memeproxy.Connection;
import me.zero.memeproxy.interfaces.Interceptor;

import java.net.Socket;

/**
 * @author Brady
 * @since 8/14/2018
 */
class ProxyConnection extends Thread {

    final int buffer;

    Interceptor interceptor;
    Connection connection;

    ProxyConnection(String name, Connection connection, int buffer) {
        super(name);
        this.connection = connection;
        this.buffer = buffer;
    }

    public final void setInterceptor(Interceptor interceptor) {
        this.interceptor = interceptor;
    }
}
