package org.hydracache.console.validate

/**
 * Created by nick.zhu
 */
class NullableValidator extends Closure {

    def NullableValidator(owner) {
        super(owner);
    }

    def doCall(propertyValue, bean, allowNull) {
        if (allowNull)
            return true

        return propertyValue != null
    }

}
