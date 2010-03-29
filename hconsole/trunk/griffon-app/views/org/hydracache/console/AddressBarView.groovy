package org.hydracache.console

import static java.awt.FlowLayout.LEFT
import static java.awt.BorderLayout.*
import org.hydracache.console.validate.ErrorMessagePanel
import net.sourceforge.gvalidation.ErrorMessagePanel

actions {
    action(id: "connectAction",
            name: messageSource.getMessage('''addressBar.connectAction.caption'''),
            closure: controller.connect)
}

errorDialog = dialog(modal: true)

panel(addressBar){
    borderLayout()
    container(new ErrorMessagePanel(messageSource), id:'errorMessagePanel', constraints: NORTH)

    panel(constraints: CENTER){
        flowLayout(alignment : LEFT)
        label(messageSource.getMessage('addressBar.address.label'))
        textField(columns: 12, text: bind(target: model, 'server'))
        label(messageSource.getMessage('addressBar.port.label'))
        textField(columns: 4, text: bind(target: model, 'port'))
        button(connectAction)
    }
}