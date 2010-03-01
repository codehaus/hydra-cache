package org.hydracache.server.httpd.handler;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.hydracache.server.Identity;
import org.hydracache.server.harmony.jgroups.JGroupsNode;
import org.hydracache.server.harmony.membership.MembershipRegistry;
import org.jgroups.stack.IpAddress;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;

public class PrintRegistryActionTest {
    @Mock
    private HttpResponse mockRequest;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void ensureCorrectRegistryPrintWithoutPadding() throws Exception {
        MembershipRegistry membershipRegistry = mockMembershipRegistry();

        ArgumentCaptor<StringEntity> captor = ArgumentCaptor
                .forClass(StringEntity.class);

        HttpServiceAction command = new PrintRegistryAction(membershipRegistry);

        command.execute(mockRequest);

        verify(mockRequest, times(1)).setEntity(captor.capture());

        String printOutput = IOUtils.toString(captor.getValue().getContent());

        assertTrue("JSON output is incorrect", printOutput
                .contains("[{\"port\":8080,"));
        assertTrue("JSON output is incorrect", printOutput
                .contains(",{\"port\":8081,"));
    }

    private MembershipRegistry mockMembershipRegistry() {
        JGroupsNode self = new JGroupsNode(new Identity(8080), new IpAddress(
                7000));

        MembershipRegistry membershipRegistry = new MembershipRegistry(self);

        membershipRegistry.register(new JGroupsNode(new Identity(8081),
                new IpAddress(7001)));
        
        return membershipRegistry;
    }

}
