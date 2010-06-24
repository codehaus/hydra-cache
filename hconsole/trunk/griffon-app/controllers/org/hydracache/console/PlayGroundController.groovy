package org.hydracache.console

import javax.swing.JFileChooser

class PlayGroundController {
    // these will be injected by Griffon
    def model
    def view

    def hydraSpaceService

    def fileChooser = new JFileChooser()

    void mvcGroupInit(Map args) {
        // this method is called after model and view are injected
    }

    def put = { evt = null ->
        if (model.validate(['storageContextToPut', 'storageKeyToPut', 'storageValueToPut'])) {
            hydraSpaceService.put(model.storageContextToPut, model.storageKeyToPut, model.storageValueToPut)
        }
    }

    def get = { evt = null ->
        if (model.validate(['storageContextToGet', 'storageKeyToGet'])) {
            model.retrievedStorageValue = hydraSpaceService.get(model.storageContextToGet, model.storageKeyToGet)
        }
    }

    def putFile = { evt = null ->
        if (model.validate(['storageContextToPut', 'storageKeyToPut', 'sourceFile'])) {
            hydraSpaceService.put(model.storageContextToPut, model.storageKeyToPut, new File(model.sourceFile).bytes)
        }
    }

    def getFile = { evt = null ->
        if (model.validate(['storageContextToGet', 'storageKeyToGet', 'targetFile'])) {
            def bytes = hydraSpaceService.get(model.storageContextToGet, model.storageKeyToGet)
            new File(model.targetFile) << bytes 
        }
    }

    def selectSourceFile = {evt = null ->
        int returnVal = fileChooser.showOpenDialog(view.playgroundPane)

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            model.sourceFile = fileChooser.selectedFile?.absolutePath
            log.debug "Selected source file: ${model.sourceFile}"
        }
    }

    def selectTargetFile = {evt = null ->
        int returnVal = fileChooser.showOpenDialog(view.playgroundPane)

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            model.targetFile = fileChooser.selectedFile?.absolutePath
            log.debug "Selected target file: ${model.targetFile}"
        }
    }
}