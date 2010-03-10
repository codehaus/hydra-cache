package org.hydracache.server.httpd.handler;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.apache.commons.io.IOUtils;
import org.apache.http.entity.StringEntity;
import org.hydracache.server.Identity;
import org.hydracache.server.harmony.jgroups.JGroupsNode;
import org.hydracache.server.harmony.membership.MembershipRegistry;
import org.jgroups.stack.IpAddress;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

public class PrintRegistryActionTest extends AbstractJsonServiceActionTest {
    @Test
    public void ensureCorrectRegistryPrintWithNullParam() throws Exception {
        MembershipRegistry membershipRegistry = mockMembershipRegistry();

        stubJSonPHandlerParam("null");

        ArgumentCaptor<StringEntity> captor = ArgumentCaptor
                .forClass(StringEntity.class);

        HttpServiceAction command = new PrintRegistryAction(membershipRegistry);

        command.execute(mockRequest, mockResponse);

        verify(mockResponse, times(1)).setEntity(captor.capture());

        String printOutput = IOUtils.toString(captor.getValue().getContent());

        assertFalse("JSON output is incorrect", printOutput.startsWith("null("));
        assertTrue("JSON output is incorrect", printOutput
                .startsWith("[{\"port\":8080,"));
        assertTrue("JSON output is incorrect", printOutput
                .contains(",{\"port\":8081,"));
    }

    @Test
    public void ensureCorrectRegistryPrintWithoutPadding() throws Exception {
        MembershipRegistry membershipRegistry = mockMembershipRegistry();

        stubRequestWithEmptyParams();

        ArgumentCaptor<StringEntity> captor = ArgumentCaptor
                .forClass(StringEntity.class);

        HttpServiceAction command = new PrintRegistryAction(membershipRegistry);

        command.execute(mockRequest, mockResponse);

        verify(mockResponse, times(1)).setEntity(captor.capture());

        String printOutput = IOUtils.toString(captor.getValue().getContent());

        assertFalse("JSON output is incorrect", printOutput.startsWith("null("));
        assertTrue("JSON output is incorrect", printOutput
                .startsWith("[{\"port\":8080,"));
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

    @Test
    public void ensureCorrectRegistryPrintWithPadding() throws Exception {
        MembershipRegistry membershipRegistry = mockMembershipRegistry();

        stubJSonPHandlerParam("testHandler");

        ArgumentCaptor<StringEntity> captor = ArgumentCaptor
                .forClass(StringEntity.class);

        HttpServiceAction command = new PrintRegistryAction(membershipRegistry);

        command.execute(mockRequest, mockResponse);

        verify(mockResponse, times(1)).setEntity(captor.capture());

        String printOutput = IOUtils.toString(captor.getValue().getContent());

        assertTrue("JSON output is missing the padding", printOutput
                .contains("testHandler([{\"port\":8080,"));
        assertTrue("JSON output is incorrect", printOutput
                .contains("[{\"port\":8080,"));
        assertTrue("JSON output is incorrect", printOutput
                .contains(",{\"port\":8081,"));
    }
}
