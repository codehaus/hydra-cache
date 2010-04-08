package org.hydracache.console

import static java.awt.FlowLayout.LEFT
import static java.awt.BorderLayout.*
import net.sourceforge.gvalidation.swing.ErrorMessagePanel
import static org.hydracache.console.ConnectionState.*
import org.apache.commons.lang.math.NumberUtils

actions {
    action(id: "connectAction",
            name: messageSource.getMessage('''addressBar.connectAction.caption'''),
            closure: controller.connect)
    action(id: "disconnectAction",
            name: messageSource.getMessage('''addressBar.disconnectAction.caption'''),
            closure: controller.disConnect)
}

panel(addressBar) {
    borderLayout()
    container(new ErrorMessagePanel(messageSource), id: 'errorMessagePanel', constraints: NORTH)

    panel(constraints: CENTER) {
        flowLayout(alignment: LEFT)
        label(messageSource.getMessage('addressBar.address.label'))
        textField(columns: 12, enabled: bind {model.connectionState == DIS_CONNECTED}, text: bind(target: model, 'server'))
        label(messageSource.getMessage('addressBar.port.label'))
        textField(columns: 4, enabled: bind {model.connectionState == DIS_CONNECTED},
                text: bind(target: model, 'port',
                        converter: {v ->
                            if (NumberUtils.isDigits(v))
                                try {
                                    return Integer.parseInt(v)
                                } catch (java.lang.NumberFormatException ex) {
                                    return -1
                                }
                            else
                                return -1
                        }))
        button(connectAction, visible: bind {model.connectionState == DIS_CONNECTED})
        button(disconnectAction, visible: bind {model.connectionState == CONNECTED})
        label(messageSource.getMessage('addressBar.connecting.label'), visible: bind {model.connectionState == CONNECTING})
    }
}