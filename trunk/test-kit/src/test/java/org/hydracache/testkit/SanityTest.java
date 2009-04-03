package org.hydracache.testkit;

import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/testkit-context.xml", "/test-context.xml" })
public class SanityTest {
    
    @Autowired
    private DataSource testKitDataSource;
    
    @Test
    public void ensureApplicationContextCanBeLoaded(){
        assertNotNull(testKitDataSource);
    }
    
}
