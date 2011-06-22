// log4j configuration
log4j = {
    appenders {
        console name: 'stdout', layout: pattern(conversionPattern: '%d [%t] %-5p %c - %m%n')
    }

    error  'org.codehaus.griffon'

    error  'griffon.util',
           'griffon.core',
           'griffon.swing',
           'griffon.app'

    debug "org.hydracache"

    environments {
        production {
            error "griffon"
            error "org.hydracache"
        }
    }
}



// look and feel config
lookandfeel{
    lookAndFeel='Substance'
    theme='MistAqua'
    keystroke = 'shift meta L'
}
