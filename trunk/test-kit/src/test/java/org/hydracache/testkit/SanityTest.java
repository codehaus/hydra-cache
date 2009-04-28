package org.hydracache.testkit;

import static org.junit.Assert.assertNotNull;

import javax.sql.DataSource;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class SanityTest extends AbstractSpringTestCase {
    
    @Autowired
    private DataSource testKitDataSource;
    
    @Test
    public void ensureApplicationContextCanBeLoaded(){
        assertNotNull(testKitDataSource);
    }
    
}
