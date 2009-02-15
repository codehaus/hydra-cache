package org.hydracache.server.httpd.handler;

import static org.hydracache.server.httpd.HttpConstants.PLAIN_TEXT_RESPONSE_CONTENT_TYPE;

import java.io.IOException;
import java.util.Collection;

import org.apache.commons.lang.SystemUtils;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.hydracache.server.data.storage.Data;
import org.hydracache.server.data.storage.DataBank;

/**
 * @author nick.zhu
 * 
 */
public class PrintStorageInfoAction implements HttpGetAction {
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
        Collection<Data> allData = internalDataBank.getAll();
        StringBuffer content = new StringBuffer();
        
        for (Data data : allData) {
            content.append("keyHash:").append(data.getKeyHash()).append(", ");
            content.append("version:").append(data.getVersion()).append(", ");
            content.append("size:").append(data.getContent().length);
            content.append(SystemUtils.LINE_SEPARATOR);
        }
        
        StringEntity body = new StringEntity(content.toString());

        body.setContentType(PLAIN_TEXT_RESPONSE_CONTENT_TYPE);

        response.setEntity(body);
    }

}
