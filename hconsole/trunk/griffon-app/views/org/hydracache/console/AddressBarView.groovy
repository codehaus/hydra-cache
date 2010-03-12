package org.hydracache.console

panel(addressBar){
    flowLayout()
    label('Entry Node Address: ')
    textField(columns: 12)
    label('Port: ')
    textField(columns: 4)
    button("Connect")
}