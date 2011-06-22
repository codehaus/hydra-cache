package org.hydracache.console

import groovy.beans.Bindable
import org.hydracache.server.Identity
import org.apache.commons.io.FileUtils

class SpaceDashboardModel {
    def serverNodes
    def storageInfo

    @Bindable int numberOfNodes
    @Bindable long totalMemory
    @Bindable long usedMemory

    def updateOverview() {
        log.debug "Updating SpaceDashboardModel ..."

        if (serverNodes) {
            if (serverNodes instanceof Identity)
                setNumberOfNodes(1)
            else
                setNumberOfNodes(serverNodes.size())
        }

        if (storageInfo) {
            int n = Integer.parseInt(storageInfo.N)

            long serverMemory = Long.parseLong(storageInfo.maxMemory)
            long totalPhysicalMem = serverMemory * numberOfNodes
            setTotalMemory((long) (totalPhysicalMem / n))

            long serverHeapMemory = Long.parseLong(storageInfo.totalMemory)
            long serverFreeHeapMemory = Long.parseLong(storageInfo.freeMemory)
            setUsedMemory((long) ((serverHeapMemory - serverFreeHeapMemory) / n))
        }
    }
}