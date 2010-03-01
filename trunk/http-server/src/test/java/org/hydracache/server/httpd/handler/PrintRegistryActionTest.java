package org.hydracache.server.httpd.handler;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.hydracache.server.Identity;
import org.hydracache.server.harmony.jgroups.JGroupsNode;
import org.hydracache.server.harmony.membership.MembershipRegistry;
import org.jgroups.stack.IpAddress;
import org.junit.Test;

public class PrintRegistryActionTest {
    private JGroupsNode self = new JGroupsNode(new Identity(8080),
            new IpAddress(7000));
    
    private HttpResponse mockRequest;

    @Test
    public void ensureCorrectRegistryPrintWithoutPadding() throws Exception {
        MembershipRegistry membershipRegistry = new MembershipRegistry(self);
        
        membershipRegistry.register(new JGroupsNode(new Identity(8081),
                new IpAddress(7001)));
        
        mockRequest = mock(HttpResponse.class);

        HttpServiceAction command = new PrintRegistryAction(membershipRegistry);
        
        command.execute(mockRequest);

        verify(mockRequest, times(1)).setEntity(any(StringEntity.class));
    }

}
