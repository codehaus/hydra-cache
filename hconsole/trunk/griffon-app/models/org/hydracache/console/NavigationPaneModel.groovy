package org.hydracache.console

import groovy.beans.Bindable
import javax.swing.ListModel
import javax.swing.AbstractListModel
import javax.swing.DefaultListModel

class NavigationPaneModel {

    ListModel serverListModel = new DefaultListModel()

    def updateServerList(nodes) {
        serverListModel.removeAllElements()

        nodes.each{
            serverListModel.addElement(it)
        }
    }

}