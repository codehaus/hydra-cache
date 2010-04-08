package org.hydracache.console

import groovy.beans.Bindable
import org.hydracache.server.Identity
import org.apache.commons.io.FileUtils

class SpaceDashboardModel {
    def serverNodes
    def storageInfo

    @Bindable int numberOfNodes
    @Bindable String totalMemory
    @Bindable String usedMemory

    def updateOverview() {
        if (serverNodes) {
            if (serverNodes instanceof Identity)
                numberOfNodes = 1
            else
                numberOfNodes = serverNodes.size()
        }

        if (storageInfo) {
            int n = Integer.parseInt(storageInfo.N)

            long serverMemory = Long.parseLong(storageInfo.maxMemory)
            long totalPhysicalMem = serverMemory * numberOfNodes
            totalMemory = FileUtils.byteCountToDisplaySize((long) (totalPhysicalMem / n))

            long serverHeapMemory = Long.parseLong(storageInfo.totalMemory)
            long serverFreeHeapMemory = Long.parseLong(storageInfo.freeMemory)
            usedMemory =  FileUtils.byteCountToDisplaySize((long) ((serverHeapMemory - serverFreeHeapMemory) / n))
        }
    }
}