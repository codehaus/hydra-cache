package org.hydracache.console

import javax.swing.JFileChooser

class PlayGroundController {
    // these will be injected by Griffon
    def model
    def view

    def hydraSpaceService

    void mvcGroupInit(Map args) {
        // this method is called after model and view are injected
    }

    def put = { evt = null ->
        if (model.validate(['storageContextToPutTxt', 'storageKeyToPutTxt', 'txtToPut'])) {
            hydraSpaceService.put(model.storageContextToPutTxt, model.storageKeyToPutTxt, model.txtToPut)
        }
    }

    def get = { evt = null ->
        if (model.validate(['storageContextToGetTxt', 'storageKeyToGetTxt'])) {
            model.retrievedTxt = hydraSpaceService.get(model.storageContextToGetTxt, model.storageKeyToGetTxt)
        }
    }

    def putFile = { evt = null ->
        if (model.validate(['storageContextToPutBin', 'storageKeyToPutBin', 'fileToPut'])) {
            hydraSpaceService.put(model.storageContextToPutBin, model.storageKeyToPutBin, new File(model.fileToPut).bytes)
        }
    }

    def getFile = { evt = null ->
        if (model.validate(['storageContextToGetBin', 'storageKeyToGetBin', 'fileToWrite'])) {
            def bytes = hydraSpaceService.get(model.storageContextToGetBin, model.storageKeyToGetBin)
            new File(model.fileToWrite) << bytes
        }
    }

    def selectSourceFile = {evt = null ->
        def fileChooser = new JFileChooser()

        int returnVal = fileChooser.showOpenDialog(view.playgroundPane)

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            model.fileToPut = fileChooser.selectedFile?.absolutePath
            log.debug "Selected source file: ${model.fileToPut}"
        }
    }

    def selectTargetFile = {evt = null ->
        def fileChooser = new JFileChooser()

        int returnVal = fileChooser.showOpenDialog(view.playgroundPane)

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            model.fileToWrite = fileChooser.selectedFile?.absolutePath
            log.debug "Selected target file: ${model.fileToWrite}"
        }
    }
}