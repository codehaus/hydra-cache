package org.hydracache.server.httpd.handler;

import static org.hydracache.server.httpd.HttpConstants.PLAIN_TEXT_RESPONSE_CONTENT_TYPE;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
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
    private static final String PORT_FIELD_NAME = "port";

    private static final String IP_FIELD_NAME = "ip";

    private static final String JSONP_CALLBACK_PARAM_NAME = "handler";

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
     * org.hydracache.server.httpd.handler.HttpServiceAction#execute(org.apache
     * .http.HttpRequest, org.apache.http.HttpResponse)
     */
    public void execute(HttpRequest request, HttpResponse response)
            throws HttpException, IOException {
        String jsonString = buildJsonNodeArray();

        String jsonHandlerParam = getJsonHandlerParam(request);

        if (isJSONPRequest(jsonHandlerParam)) {
            jsonString = padJSONResponse(jsonString, jsonHandlerParam);
        }

        StringEntity body = new StringEntity(jsonString);

        body.setContentType(PLAIN_TEXT_RESPONSE_CONTENT_TYPE);

        response.setEntity(body);
    }

    private boolean isJSONPRequest(String jsonHandlerParam) {
        return StringUtils.isNotBlank(jsonHandlerParam);
    }

    private String padJSONResponse(String jsonString, String jsonHandlerParam) {
        jsonString = jsonHandlerParam + "(" + jsonString + ")";
        return jsonString;
    }

    private String getJsonHandlerParam(HttpRequest request) {
        return request.getParams() == null ? "" : String.valueOf(request
                .getParams().getParameter(JSONP_CALLBACK_PARAM_NAME));
    }

    private String buildJsonNodeArray() {
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
