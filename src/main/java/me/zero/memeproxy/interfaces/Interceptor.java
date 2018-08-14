package me.zero.memeproxy.interfaces;

import me.zero.memeproxy.Connection;

import java.nio.ByteBuffer;
import java.util.Queue;

/**
 * @author Brady
 * @since 8/13/2018
 */
public interface Interceptor {

    boolean clientToServer(ByteBuffer msg, Connection connection);

    boolean serverToClient(ByteBuffer msg, Connection connection);

    Queue<ByteBuffer> getClientSendQueue();

    Queue<ByteBuffer> getServerSendQueue();
}
