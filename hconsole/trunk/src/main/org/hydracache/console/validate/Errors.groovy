package org.hydracache.console.validate

import org.apache.commons.lang.builder.HashCodeBuilder
import org.apache.commons.lang.builder.EqualsBuilder

/**
 * Created by nick.zhu
 */
class Errors {
    def fieldErrors = [:]

    def rejectValue(field, errorCode) {
        rejectValue(field, errorCode, [])
    }

    def rejectValue(field, errorCode, arguments){
        fieldErrors[field] = new FieldError(field: field, errorCode: errorCode, arguments: arguments)
    }

    def hasFieldErrors(){
        return fieldErrors.size() > 0
    }

    def hasFieldErrors(field){
        return fieldErrors[field] != null
    }

    def getFieldError(field){
        return fieldErrors[field]
    }
}

class FieldError {
    def field
    def errorCode
    def arguments
}
