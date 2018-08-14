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

package me.zero.memeproxy.connection;

import me.zero.memeproxy.Connection;
import me.zero.memeproxy.interfaces.Interceptor;

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
