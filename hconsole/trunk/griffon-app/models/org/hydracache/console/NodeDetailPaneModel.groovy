package org.hydracache.console

import groovy.beans.Bindable
import org.hydracache.server.Identity

class NodeDetailPaneModel {
    def storageInfo

    @Bindable Identity server
    @Bindable long maxMemory
    @Bindable long freeMemory
    @Bindable long heapMemory
    @Bindable long usedMemory

    def updateDetails() {
        setMaxMemory(Long.parseLong(storageInfo.maxMemory))
        setFreeMemory(Long.parseLong(storageInfo.freeMemory))
        setHeapMemory(Long.parseLong(storageInfo.totalMemory))
        setUsedMemory(heapMemory - freeMemory)
    }
}