/**
 *  Copyright (c) 2015 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */


package org.wso2.carbon.bpmn.rest.common.provider.ExceptionMapper;

import org.activiti.engine.ActivitiIllegalArgumentException;
import org.activiti.engine.ActivitiObjectNotFoundException;
import org.activiti.engine.ActivitiTaskAlreadyClaimedException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.bpmn.rest.common.exception.BPMNOSGIServiceException;
import org.wso2.carbon.bpmn.rest.common.security.RestErrorResponse;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class BPMNExceptionHandler implements ExceptionMapper<Exception> {

    private final Log log = LogFactory.getLog(BPMNExceptionHandler.class);
    @Override
    public Response toResponse(Exception e) {

         new RestErrorResponse();

        if(e instanceof ActivitiIllegalArgumentException){
            log.error("Exception during service invocation ", e);
            return createRestErrorResponse(Response.Status.BAD_REQUEST, e.getMessage());
        } else if(e instanceof ActivitiTaskAlreadyClaimedException){
            log.error("Exception during Task claiming ", e);
            return createRestErrorResponse(Response.Status.CONFLICT, e.getMessage());
        } else if(e instanceof ActivitiObjectNotFoundException){
            log.error("Exception due to Activiti Object Not Found ", e);
            return createRestErrorResponse(Response.Status.NOT_FOUND, e.getMessage());
        } else if(e instanceof BPMNOSGIServiceException){
            log.error("Exception due to issues on osgi service ", e);
            return createRestErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Exception due to issues on osgi " +
                    "service");
        } else if(e instanceof NotFoundException){
            log.error("unsupported operation not found ", e);
            return createRestErrorResponse(Response.Status.NOT_FOUND,"unsupported operation");
        } else if(e instanceof ClientErrorException){
            log.error("unsupported operation not found ", e);
            return createRestErrorResponse(Response.Status.NOT_FOUND, "Failed to hit the request target due to " +
                    "unsupported operation");
        } else if(e instanceof WebApplicationException){
            log.error("Web application exception thrown ", e);
            return createRestErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Web application exception thrown");
        } else {
            log.error("Unknown Exception occurred ", e);
            return createRestErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private Response createRestErrorResponse(Response.Status statusCode, String message){
        RestErrorResponse restErrorResponse = new RestErrorResponse();
        restErrorResponse.setStatusCode(statusCode.getStatusCode());
        restErrorResponse.setErrorMessage(message);

        return Response.status(statusCode).entity(restErrorResponse).build();
    }
}
