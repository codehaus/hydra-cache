package org.hydracache.server.httpd.handler;

import static org.hydracache.server.httpd.HttpConstants.PLAIN_TEXT_RESPONSE_CONTENT_TYPE;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.log4j.Logger;
import org.hydracache.server.Identity;
import org.hydracache.server.harmony.core.Node;
import org.hydracache.server.harmony.core.NodeSet;
import org.hydracache.server.harmony.membership.MembershipRegistry;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author nick.zhu
 * 
 */
public class PrintRegistryAction implements HttpServiceAction {
    private static Logger log = Logger.getLogger(PrintRegistryAction.class);

    private MembershipRegistry membershipRegistry;

    public PrintRegistryAction(MembershipRegistry membershipRegistry) {
        super();
        this.membershipRegistry = membershipRegistry;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.hydracache.server.httpd.handler.HttpGetCommand#getRequestContext()
     */
    public String getName() {
        return "registry";
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.hydracache.server.httpd.handler.HttpGetCommand#handle(org.apache.
     * http.HttpResponse)
     */
    public void execute(HttpResponse response) throws HttpException,
            IOException {
        JSONArray nodeArray = new JSONArray();

        NodeSet allNodes = membershipRegistry.listAllMembers();

        try {
            for (Node node : allNodes) {
                Identity nodeId = node.getId();
                nodeArray.put(new JSONObject().put("ip",
                        nodeId.getAddress().getHostAddress()).put("port",
                        nodeId.getPort()));
            }
        } catch (Exception ex) {
            log.error("Failed to generate node list using JSON", ex);
        }

        StringEntity body = new StringEntity(nodeArray.toString());

        body.setContentType(PLAIN_TEXT_RESPONSE_CONTENT_TYPE);

        response.setEntity(body);
    }

}
