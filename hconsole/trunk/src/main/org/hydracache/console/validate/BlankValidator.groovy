package org.hydracache.console.validate

import org.apache.commons.lang.StringUtils

/**
 * Created by nick.zhu
 */
class BlankValidator extends Closure {

    def BlankValidator(owner) {
        super(owner);
    }

    def doCall(propertyValue, bean, allowBlank) {
        if (allowBlank) {
            return propertyValue != null
        } else {
            return StringUtils.isNotBlank(propertyValue)
        }
    }
}
