package org.hydracache.console

class PlayGroundController {
    // these will be injected by Griffon
    def model
    def view

    def hydraSpaceService

    void mvcGroupInit(Map args) {
        // this method is called after model and view are injected
    }

    def put = { evt = null ->
        if(model.validate(['storageContextToPut', 'storageKeyToPut', 'storageValueToPut'])){
            hydraSpaceService.put(model.storageContextToPut, model.storageKeyToPut, model.storageValueToPut)            
        }
    }

    def get = { evt = null ->
        if(model.validate(['storageContextToGet', 'storageKeyToGet'])){
            model.retrievedStorageValue = hydraSpaceService.get(model.storageContextToGet, model.storageKeyToGet)
        }
    }
}