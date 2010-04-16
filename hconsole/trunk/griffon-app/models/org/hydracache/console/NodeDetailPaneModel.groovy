package org.hydracache.console

import groovy.beans.Bindable

class NodeDetailPaneModel {
    def storageInfo

    @Bindable String maxMemory
    @Bindable String freeMemory
    @Bindable String heapMemory
    @Bindable String usedMemory

    def updateDetails(){
        
    }
}