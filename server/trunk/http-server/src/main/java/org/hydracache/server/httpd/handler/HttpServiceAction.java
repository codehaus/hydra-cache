package org.hydracache.server.httpd.handler;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

public interface HttpServiceAction {

    public abstract String getName();

    public abstract void execute(HttpRequest request, HttpResponse response)
            throws HttpException, IOException;

}