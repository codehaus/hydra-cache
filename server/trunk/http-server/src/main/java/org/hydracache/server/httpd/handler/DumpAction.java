package org.hydracache.server.httpd.handler;

import java.io.IOException;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.hydracache.server.data.storage.Data;
import org.hydracache.server.data.storage.DataBank;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Nick Zhu (nzhu@jointsource.com)
 * 
 */
public class DumpAction extends BaseJsonServiceAction implements
        HttpServiceAction {
    private static final String SIZE_FIELD = "size";

    private static final String VERSION_FIELD = "version";

    private static final String KEY_HASH_FIELD = "keyHash";

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
     * org.hydracache.server.httpd.handler.BaseJsonServiceAction#buildJsonOutput
     * ()
     */
    @Override
    protected String buildJsonOutput() throws IOException {
        Collection<Data> allData = internalDataBank.getAll();

        String content = printDataSet(allData);

        return content;
    }

    String printDataSet(Collection<Data> allData) {
        JSONArray outputArray = new JSONArray();

        try {
            for (Data data : allData) {
                if (data != null) {
                    JSONObject row = new JSONObject();

                    row.put(KEY_HASH_FIELD, data.getKeyHash());
                    row.put(VERSION_FIELD, data.getVersion());
                    row.put(SIZE_FIELD, data.getContent() == null ? 0 : data
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
