package org.hydracache.console

import static java.awt.FlowLayout.LEFT

actions {
    action(id: "connectAction",
            name: messageSource.getMessage('''addressBar.connectAction.caption'''),
            closure: controller.connect)
}

panel(addressBar){
    flowLayout(alignment : LEFT)
    label(messageSource.getMessage('addressBar.address.label'))
    textField(columns: 12, text: bind(target: model, 'server'))
    label(messageSource.getMessage('addressBar.port.label'))
    textField(columns: 4, text: bind(target: model, 'port'))
    button(connectAction)
}