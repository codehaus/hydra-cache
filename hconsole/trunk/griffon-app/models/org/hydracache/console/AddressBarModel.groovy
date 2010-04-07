package org.hydracache.console

import groovy.beans.Bindable

class AddressBarModel {
    @Bindable String server
    @Bindable int port
    @Bindable boolean connected

    static constraints = {
        server(blank: false, inetAddress: true)
        port(range: 1..65535)
    }
}