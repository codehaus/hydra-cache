package org.hydracache.console.validate

/**
 * Created by nick.zhu
 */
class Errors {
    def fieldErrors = [:]
    def globalErrors = []

    def reject(errorCode){
        reject(errorCode, [])
    }

    def reject(errorCode, arguments){
        globalErrors.add(new SimpleError(errorCode: errorCode, arguments: arguments))
    }

    def hasGlobalErrors(){
        return !globalErrors.isEmpty()
    }

    def rejectValue(field, errorCode) {
        rejectValue(field, errorCode, [])
    }

    def rejectValue(field, errorCode, arguments){
        fieldErrors[field] = new FieldError(field: field, errorCode: errorCode, arguments: arguments)
    }

    def hasFieldErrors(){
        return !fieldErrors.isEmpty()
    }

    def hasFieldErrors(field){
        return fieldErrors[field] != null
    }

    def getFieldError(field){
        return fieldErrors[field]
    }

    def hasErrors(){
        return hasGlobalErrors() || hasFieldErrors()
    }

    def clear(){
        fieldErrors.clear()
        globalErrors.clear()
    }
}

class FieldError extends SimpleError {
    def field
}

class SimpleError {
    def errorCode
    def arguments
}