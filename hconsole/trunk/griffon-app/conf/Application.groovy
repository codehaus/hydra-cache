application {
    title='Hconsole'
    startupGroups = ['Hconsole']

    // Should Griffon exit when no Griffon created frames are showing?
    autoShutdown = true

    // If you want some non-standard application class, apply it here
    //frameClass = 'javax.swing.JFrame'
}
mvcGroups {
    // MVC Group for "org.hydracache.console.AddressBar"
    'AddressBar' {
        model = 'org.hydracache.console.AddressBarModel'
        controller = 'org.hydracache.console.AddressBarController'
        view = 'org.hydracache.console.AddressBarView'
    }

    // MVC Group for "org.hydracache.console.NavigationPane"
    'NavigationPane' {
        model = 'org.hydracache.console.NavigationPaneModel'
        controller = 'org.hydracache.console.NavigationPaneController'
        view = 'org.hydracache.console.NavigationPaneView'
    }

    // MVC Group for "Hconsole"
    'Hconsole' {
        model = 'org.hydracache.console.HconsoleModel'
        view = 'org.hydracache.console.HconsoleView'
        controller = 'org.hydracache.console.HconsoleController'
    }

}