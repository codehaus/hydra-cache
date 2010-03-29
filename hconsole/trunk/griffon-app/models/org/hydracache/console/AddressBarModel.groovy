package org.hydracache.console

import groovy.beans.Bindable

class AddressBarModel {
    @Bindable String server
    @Bindable String port

    static constraints = {
        server(blank: false)
        port(blank: false)
    }
}