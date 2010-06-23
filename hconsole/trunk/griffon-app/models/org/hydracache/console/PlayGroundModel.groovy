package org.hydracache.console

import groovy.beans.Bindable

class PlayGroundModel {
    @Bindable String storageContextToPut
    @Bindable String storageKeyToPut
    @Bindable String storageValueToPut

    @Bindable String storageContextToGet
    @Bindable String storageKeyToGet
    @Bindable String retrievedStorageValue

    static def constraints = {
        storageContextToPut(nullable: false, blank: true, maxSize: 32)
        storageKeyToPut(blank: false, maxSize: 64)
        storageValueToPut(blank: false, maxSize: 256)

        storageContextToGet(nullable: false, blank: true, maxSize: 32)
        storageKeyToGet(blank: false, maxSize: 64)
        retrievedStorageValue(maxSize: 256)            
    }
}