package org.hydracache.server.harmony.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

public final class TestUtils {
    public static InetAddress getStableLocalAddress()
            throws UnknownHostException {
        return Inet4Address.getByName("127.0.0.1");
    }
}
