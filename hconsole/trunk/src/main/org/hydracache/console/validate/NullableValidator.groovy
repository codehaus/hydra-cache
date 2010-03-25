package org.hydracache.console.validate

/**
 * Created by nick.zhu
 */
class NullableValidator extends Closure {

    def NullableValidator(owner) {
        super(owner);
    }

    def doCall(value, delegate, config) {
        if (config)
            return true

        return value != null
    }

}
