package org.hydracache.console

/**
 * Created by nick.zhu
 */

class GeneralTests extends GriffonTestCase {
    def v = 100
    def gString = "$v"
    def string = "100"

    public void testGStrings() {
        assertFalse "Hash code value is different", gString.hashCode() == string.hashCode()
        assertEquals gString, string
        assertEquals string, gString
        assertFalse string.equals(gString)
        assertFalse gString.equals(string)
        assertTrue string == gString
        assertTrue gString == string
    }

    public void testHashMap() {
        def expectedValue = "value"
        def map = [:]
        map[string] = expectedValue

        assertEquals "Get value incorrect", expectedValue, map[gString]
        assertNull "Should not be removed", map.remove(gString)
    }


}