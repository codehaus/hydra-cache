package org.hydracache.server.httpd.handler;

import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.hydracache.server.Identity;
import org.hydracache.server.harmony.jgroups.JGroupsNode;
import org.hydracache.server.harmony.membership.MembershipRegistry;
import org.jgroups.stack.IpAddress;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

public class PrintRegistryActionTest {
    private Mockery context = new Mockery();

    @Test
    public void ensureCorrectRegistryPrint() throws Exception {
        MembershipRegistry membershipRegistry = new MembershipRegistry();
        membershipRegistry.register(new JGroupsNode(new Identity(8080),
                new IpAddress(7000)));
        membershipRegistry.register(new JGroupsNode(new Identity(8081),
                new IpAddress(7001)));

        final HttpResponse response = context.mock(HttpResponse.class);

        context.checking(new Expectations() {
            {
                one(response).setEntity(with(any(StringEntity.class)));
            }
        });

        HttpGetAction command = new PrintRegistryAction(membershipRegistry);
        command.execute(response);

        context.assertIsSatisfied();
    }

}
