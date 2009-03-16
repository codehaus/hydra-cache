package org.hydracache.server.httpd.handler;

import static org.hydracache.server.httpd.HttpConstants.PLAIN_TEXT_RESPONSE_CONTENT_TYPE;

import java.io.IOException;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.log4j.Logger;
import org.hydracache.server.data.storage.Data;
import org.hydracache.server.data.storage.DataBank;
import org.json.JSONObject;

/**
 * @author nick.zhu
 * 
 */
public class PrintStorageInfoAction implements HttpGetAction {
    private static Logger log = Logger.getLogger(PrintStorageInfoAction.class);

    private DataBank internalDataBank;

    public PrintStorageInfoAction(DataBank internalDataBank) {
        this.internalDataBank = internalDataBank;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hydracache.server.httpd.handler.HttpGetCommand#getName()
     */
    public String getName() {
        return "storage";
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
        JSONObject output = new JSONObject();

        Collection<Data> allData = internalDataBank.getAll();

        try {
            Runtime runtime = Runtime.getRuntime();
            output.put("size", allData.size());
            output.put("totalMemory", FileUtils.byteCountToDisplaySize(runtime.totalMemory()));
            output.put("maxMemory", FileUtils.byteCountToDisplaySize(runtime.maxMemory()));
            output.put("freeMemory", FileUtils.byteCountToDisplaySize(runtime.freeMemory()));
        } catch (Exception e) {
            log.error("Failed to print storage info", e);
        }
        
        StringEntity body = new StringEntity(output.toString());

        body.setContentType(PLAIN_TEXT_RESPONSE_CONTENT_TYPE);

        response.setEntity(body);
    }

}
