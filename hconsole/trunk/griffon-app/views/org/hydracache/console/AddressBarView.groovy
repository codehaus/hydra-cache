package org.hydracache.console

import static java.awt.FlowLayout.LEFT

panel(addressBar){
    flowLayout(alignment : LEFT)
    label(messageSource.getMessage('addressBar.address.label'))
    textField(columns: 12)
    label(messageSource.getMessage('addressBar.port.label'))
    textField(columns: 4)
    button(messageSource.getMessage('addressBar.connectBtn.caption'))
}