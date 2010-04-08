package org.hydracache.console

import groovy.beans.Bindable
import org.hydracache.server.Identity

class SpaceDashboardModel {
    def serverNodes
    def storageInfo

    @Bindable int numberOfNodes
    @Bindable long totalMemory

    def updateOverview() {
        if (serverNodes) {
            if (serverNodes instanceof Identity)
                numberOfNodes = 1
            else
                numberOfNodes = serverNodes.size()
        }

        if (storageInfo) {
            def serverMemory = storageInfo.maxMemory.replace('MB', '').trim()
            totalMemory = Long.parseLong(serverMemory) * numberOfNodes
        }
    }
}