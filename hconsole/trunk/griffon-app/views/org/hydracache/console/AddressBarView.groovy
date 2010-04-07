package org.hydracache.console

import static java.awt.FlowLayout.LEFT
import static java.awt.BorderLayout.*
import net.sourceforge.gvalidation.swing.ErrorMessagePanel

actions {
    action(id: "connectAction",
            name: messageSource.getMessage('''addressBar.connectAction.caption'''),
            closure: controller.connect)
    action(id: "disconnectAction",
            name: messageSource.getMessage('''addressBar.disconnectAction.caption'''),
            closure: controller.disConnect)
}

panel(addressBar){
    borderLayout()
    container(new ErrorMessagePanel(messageSource), id:'errorMessagePanel', constraints: NORTH)

    panel(constraints: CENTER){
        flowLayout(alignment : LEFT)
        label(messageSource.getMessage('addressBar.address.label'))
        textField(columns: 12, enabled: bind{!model.connected}, text: bind(target: model, 'server'))
        label(messageSource.getMessage('addressBar.port.label'))
        textField(columns: 4, enabled: bind{!model.connected}, text: bind(target: model, 'port', converter: Integer.&parseInt))
        button(connectAction, visible: bind{!model.connected})
        button(disconnectAction, visible: bind{model.connected})
    }
}