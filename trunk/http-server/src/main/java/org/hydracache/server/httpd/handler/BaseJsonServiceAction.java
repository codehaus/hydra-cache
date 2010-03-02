/*
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hydracache.server.httpd.handler;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpRequest;
import org.apache.log4j.Logger;

/**
 * Base http service action class with support and utility methods for JSON and
 * JSONP based protocol
 * 
 * @author Nick Zhu (nzhu@jointsource.com)
 */
public class BaseJsonServiceAction {

    public static final String JSONP_CALLBACK_PARAM_NAME = "handler";

    protected static Logger log = Logger.getLogger(PrintRegistryAction.class);

    public BaseJsonServiceAction() {
        super();
    }

    protected boolean isJSONPRequest(String jsonHandlerParam) {
        return StringUtils.isNotBlank(jsonHandlerParam);
    }

    protected String padJSONResponse(String jsonString, String jsonHandlerParam) {
        jsonString = jsonHandlerParam + "(" + jsonString + ")";
        return jsonString;
    }

    protected String getJsonHandlerParam(HttpRequest request) {
        return request.getParams() == null ? "" : String.valueOf(request
                .getParams().getParameter(JSONP_CALLBACK_PARAM_NAME));
    }

}