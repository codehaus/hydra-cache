package org.hydracache.server.httpd.handler;

import static org.hydracache.server.httpd.HttpConstants.PLAIN_TEXT_RESPONSE_CONTENT_TYPE;

import java.io.IOException;

import org.apache.commons.lang.SystemUtils;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.hydracache.server.harmony.core.Node;
import org.hydracache.server.harmony.core.NodeSet;
import org.hydracache.server.harmony.membership.MembershipRegistry;

/**
 * @author nick.zhu
 * 
 */
public class PrintRegistryAction implements HttpGetAction {
    private MembershipRegistry membershipRegistry;

    public PrintRegistryAction(MembershipRegistry membershipRegistry) {
        super();
        this.membershipRegistry = membershipRegistry;
    }
    
    /* (non-Javadoc)
     * @see org.hydracache.server.httpd.handler.HttpGetCommand#getRequestContext()
     */
    public String getName(){
        return "registry";
    }

    /* (non-Javadoc)
     * @see org.hydracache.server.httpd.handler.HttpGetCommand#handle(org.apache.http.HttpResponse)
     */
    public void execute(HttpResponse response) throws HttpException,
            IOException {
        StringBuffer content = new StringBuffer();

        NodeSet allNodes = membershipRegistry.listAllMembers();

        for (Node node : allNodes) {
            content.append(node.getId().toString());
            content.append(SystemUtils.LINE_SEPARATOR);
        }

        StringEntity body = new StringEntity(content.toString());

        body.setContentType(PLAIN_TEXT_RESPONSE_CONTENT_TYPE);

        response.setEntity(body);
    }

}
