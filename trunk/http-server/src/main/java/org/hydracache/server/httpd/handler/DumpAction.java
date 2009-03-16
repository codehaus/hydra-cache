package org.hydracache.server.httpd.handler;

import static org.hydracache.server.httpd.HttpConstants.PLAIN_TEXT_RESPONSE_CONTENT_TYPE;

import java.io.IOException;
import java.util.Collection;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.log4j.Logger;
import org.hydracache.server.data.storage.Data;
import org.hydracache.server.data.storage.DataBank;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author nick.zhu
 * 
 */
public class DumpAction implements HttpGetAction {
    private static Logger log = Logger.getLogger(DumpAction.class);

    private DataBank internalDataBank;

    public DumpAction(DataBank internalDataBank) {
        this.internalDataBank = internalDataBank;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hydracache.server.httpd.handler.HttpGetCommand#getName()
     */
    public String getName() {
        return "dump";
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.hydracache.server.httpd.handler.HttpGetCommand#execute(org.apache
     * .http.HttpResponse)
     */
    public void execute(HttpResponse response) throws HttpException,
            IOException {
        Collection<Data> allData = internalDataBank.getAll();

        String content = printDataSet(allData);

        StringEntity body = new StringEntity(content);

        body.setContentType(PLAIN_TEXT_RESPONSE_CONTENT_TYPE);

        response.setEntity(body);
    }

    String printDataSet(Collection<Data> allData) {
        JSONArray outputArray = new JSONArray();

        try {
            for (Data data : allData) {
                if (data != null) {
                    JSONObject row = new JSONObject();

                    row.put("keyHash", data.getKeyHash());
                    row.put("version", data.getVersion());
                    row.put("size", data.getContent() == null ? 0 : data
                            .getContent().length);

                    outputArray.put(row);
                }
            }
        } catch (JSONException e) {
            log.error("Failed to generate data dump", e);
        }

        return outputArray.toString();
    }

}
