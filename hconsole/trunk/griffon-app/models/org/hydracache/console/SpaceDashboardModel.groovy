package org.hydracache.console

import groovy.beans.Bindable
import org.hydracache.server.Identity
import org.apache.commons.io.FileUtils

class SpaceDashboardModel {
    def serverNodes
    def storageInfo

    @Bindable int numberOfNodes
    @Bindable String totalMemory

    def updateOverview() {
        if (serverNodes) {
            if (serverNodes instanceof Identity)
                numberOfNodes = 1
            else
                numberOfNodes = serverNodes.size()
        }

        if (storageInfo) {
            long serverMemory = storageInfo.maxMemory
            long totalPhysicalMem = serverMemory * numberOfNodes
            int n = storageInfo.N
            totalMemory = FileUtils.byteCountToDisplaySize((long) (totalPhysicalMem / n))
        }
    }
}