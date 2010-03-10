package org.hydracache.server.httpd.handler;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;

public interface HttpGetAction {

    public abstract String getName();

    public abstract void execute(HttpResponse response) throws HttpException,
            IOException;

}