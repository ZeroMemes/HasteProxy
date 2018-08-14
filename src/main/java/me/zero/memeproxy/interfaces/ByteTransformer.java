package me.zero.memeproxy.interfaces;

import java.nio.ByteBuffer;

/**
 * @author Brady
 * @since 8/14/2018
 */
@FunctionalInterface
public interface ByteTransformer {

    ByteBuffer apply(ByteBuffer in);
}
