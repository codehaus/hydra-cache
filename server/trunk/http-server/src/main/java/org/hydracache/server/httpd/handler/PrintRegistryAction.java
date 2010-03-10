package org.hydracache.server.httpd.handler;

import java.io.IOException;

import org.hydracache.server.Identity;
import org.hydracache.server.harmony.core.Node;
import org.hydracache.server.harmony.core.NodeSet;
import org.hydracache.server.harmony.membership.MembershipRegistry;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Http service action that generate Hydra space registry information in either
 * JSON or JSONP format for consumption
 * 
 * @author Nick Zhu (nzhu@jointsource.com)
 */
public class PrintRegistryAction extends BaseJsonServiceAction implements
        HttpServiceAction {
    private static final String PORT_FIELD_NAME = "port";

    private static final String IP_FIELD_NAME = "ip";

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
     * org.hydracache.server.httpd.handler.BaseJsonServiceAction#buildJsonOutput
     * ()
     */
    @Override
    protected String buildJsonOutput() throws IOException {
        JSONArray nodeArray = new JSONArray();

        NodeSet allNodes = membershipRegistry.listAllMembers();

        try {
            for (Node node : allNodes) {
                Identity nodeId = node.getId();
                nodeArray.put(new JSONObject().put(IP_FIELD_NAME,
                        nodeId.getAddress().getHostAddress()).put(
                        PORT_FIELD_NAME, nodeId.getPort()));
            }
        } catch (Exception ex) {
            log.error("Failed to generate node list using JSON", ex);
        }

        String jsonString = nodeArray.toString();

        return jsonString;
    }

}
