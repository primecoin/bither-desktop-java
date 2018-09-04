/* * Copyright 2014 http://Bither.net * * Licensed under the Apache License, Version 2.0 (the "License"); * you may not use this file except in compliance with the License. * You may obtain a copy of the License at * *    http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */package org.primer.primerj.api.http;/** * the statusCode of http is 403,server refuse request * * @author jjz */public class HttpRefusedException extends HttpException {    /**     *     */    private static final long serialVersionUID = 1L;    private String refuseError;    public HttpRefusedException(String msg, int statusCode) {        super(msg, statusCode);    }    public HttpRefusedException(String msg, Exception cause, int statusCode) {        super(msg, cause, statusCode);    }    public HttpRefusedException(String msg, Exception cause) {        super(msg, cause);    }    public HttpRefusedException(String msg) {        super(msg);    }    public String getRefuseError() {        return refuseError;    }    public HttpRefusedException setRefuseError(String refuseError) {        this.refuseError = refuseError;        return this;    }}