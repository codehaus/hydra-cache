package org.hydracache.console

import groovy.beans.Bindable
import net.sourceforge.gvalidation.annotation.Validatable

@Validatable
class PlayGroundModel {
    @Bindable String storageContextToPutTxt
    @Bindable String storageKeyToPutTxt
    @Bindable String txtToPut

    @Bindable String storageContextToGetTxt
    @Bindable String storageKeyToGetTxt
    @Bindable String retrievedTxt


    @Bindable String storageContextToPutBin
    @Bindable String storageKeyToPutBin
    @Bindable String fileToPut


    @Bindable String storageContextToGetBin
    @Bindable String storageKeyToGetBin
    @Bindable String fileToWrite

    static def constraints = {
        storageContextToPutTxt(nullable: false, blank: true, maxSize: 32)
        storageKeyToPutTxt(blank: false, maxSize: 64)
        txtToPut(blank: false, maxSize: 256)

        storageContextToGetTxt(nullable: false, blank: true, maxSize: 32)
        storageKeyToGetTxt(blank: false, maxSize: 64)
        retrievedTxt(maxSize: 256)


        storageContextToPutBin(nullable: false, blank: true, maxSize: 32)
        storageKeyToPutBin(blank: false, maxSize: 64)
        fileToPut(blank: false)

        storageContextToGetBin(nullable: false, blank: true, maxSize: 32)
        storageKeyToGetBin(blank: false, maxSize: 64)
        fileToWrite(blank: false)
    }
}