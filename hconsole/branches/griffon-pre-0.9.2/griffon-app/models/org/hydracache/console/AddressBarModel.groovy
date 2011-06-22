package org.hydracache.console

import groovy.beans.Bindable
import static org.hydracache.console.ConnectionState.*

class AddressBarModel {
    @Bindable String server
    @Bindable int port

    @Bindable ConnectionState connectionState = DIS_CONNECTED

    static constraints = {
        server(blank: false, inetAddress: true)
        port(min: 1, max: 65535)
    }

}

enum ConnectionState {
    CONNECTED, CONNECTING, DIS_CONNECTED
}