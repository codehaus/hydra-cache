package org.hydracache.server.httpd.handler;

import java.io.IOException;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.hydracache.server.data.storage.Data;
import org.hydracache.server.data.storage.DataBank;
import org.json.JSONObject;

/**
 * Http service action that prints storage information in json format
 *
 * @author Nick Zhu (nzhu@jointsource.com)
 */
public class PrintStorageInfoAction extends BaseJsonServiceAction implements
        HttpServiceAction {
    private static final String W = "W";

    private static final String R = "R";
    
    private static final String N = "N";

    private static final String FREE_MEMORY_FIELD = "freeMemory";

    private static final String MAX_MEMORY_FIELD = "maxMemory";

    private static final String TOTAL_MEMORY_FIELD = "totalMemory";

    private static final String SIZE_FIELD = "size";

    private static Logger log = Logger.getLogger(PrintStorageInfoAction.class);

    private DataBank internalDataBank;

    private int hopN;
    private int hopW;
    private int hopR;


    public PrintStorageInfoAction(DataBank internalDataBank) {
        this.internalDataBank = internalDataBank;
    }

    public void setHopN(int hopN) {
        this.hopN = hopN;
    }

    public void setHopW(int hopW) {
        this.hopW = hopW;
    }

    public void setHopR(int hopR) {
        this.hopR = hopR;
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
     * org.hydracache.server.httpd.handler.BaseJsonServiceAction#buildJsonOutput
     * ()
     */

    @Override
    protected String buildJsonOutput() throws IOException {
        JSONObject output = new JSONObject();

        Collection<Data> allData = internalDataBank.getAll();

        try {
            Runtime runtime = Runtime.getRuntime();
            output.put(SIZE_FIELD, allData.size());
            output.put(TOTAL_MEMORY_FIELD, runtime.totalMemory());
            output.put(MAX_MEMORY_FIELD, runtime.maxMemory());
            output.put(FREE_MEMORY_FIELD, runtime.freeMemory());
            output.put(W, hopW);
            output.put(R, hopR);
            output.put(N, hopN);
        } catch (Exception e) {
            log.error("Failed to print storage info", e);
        }

        String jsonOutputString = output.toString();

        return jsonOutputString;
    }

}
