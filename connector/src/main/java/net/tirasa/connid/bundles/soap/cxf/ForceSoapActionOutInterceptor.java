/* 
 * ====================
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2011 ConnId. All rights reserved.
 * 
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License("CDDL") (the "License").  You may not use this file
 * except in compliance with the License.
 * 
 * You can obtain a copy of the License at
 * http://opensource.org/licenses/cddl1.php
 * See the License for the specific language governing permissions and limitations
 * under the License.
 * 
 * When distributing the Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://opensource.org/licenses/cddl1.php.
 * If applicable, add the following below this CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * ====================
 */
package net.tirasa.connid.bundles.soap.cxf;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.commons.lang.StringUtils;

import org.apache.cxf.binding.soap.Soap11;
import org.apache.cxf.binding.soap.Soap12;
import org.apache.cxf.binding.soap.SoapBindingConstants;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.binding.soap.model.SoapOperationInfo;
import org.apache.cxf.helpers.CastUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.service.model.BindingOperationInfo;

/**
 * This interceptor is responsible for setting up the SOAP version and header, so that this is available to any
 * pre-protocol interceptors that require these to be available.
 */
public class ForceSoapActionOutInterceptor extends AbstractSoapInterceptor {

    private final String SOAPActionUriPrefix;

    public ForceSoapActionOutInterceptor(final String SOAPActionUriPrefix) {
        super(Phase.POST_LOGICAL);

        this.SOAPActionUriPrefix = SOAPActionUriPrefix == null
                ? null
                : (SOAPActionUriPrefix.endsWith("/") ? SOAPActionUriPrefix : SOAPActionUriPrefix + "/");
    }

    /**
     * Mediate a message dispatch.
     *
     * @param message the current message
     * @throws Fault
     */
    @Override
    public void handleMessage(final SoapMessage message)
            throws Fault {

        setSoapAction(message);
    }

    private void setSoapAction(final SoapMessage message) {
        BindingOperationInfo boi = message.getExchange().getBindingOperationInfo();

        // The soap action is set on the wrapped operation.
        if (boi != null && boi.isUnwrapped()) {
            boi = boi.getWrappedOperation();
        }

        final String action = getSoapAction(message, boi);

        if (message.getVersion() instanceof Soap11) {
            Map<String, List<String>> reqHeaders = CastUtils.cast((Map<?, ?>) message.get(Message.PROTOCOL_HEADERS));
            if (reqHeaders == null) {
                reqHeaders = new TreeMap<String, List<String>>(String.CASE_INSENSITIVE_ORDER);
            }

            if (reqHeaders.isEmpty()) {
                message.put(Message.PROTOCOL_HEADERS, reqHeaders);
            }

            if (!reqHeaders.containsKey(SoapBindingConstants.SOAP_ACTION)) {
                reqHeaders.put(SoapBindingConstants.SOAP_ACTION, Collections.singletonList(action));
            }
        } else if (message.getVersion() instanceof Soap12 && !"\"\"".equals(action)) {
            String contentType = (String) message.get(Message.CONTENT_TYPE);
            if (contentType.indexOf("action=\"") == -1) {
                contentType = new StringBuilder().append(contentType).append("; action=").append(action).toString();
                message.put(Message.CONTENT_TYPE, contentType);
            }
        }
    }

    private String getSoapAction(final SoapMessage message, BindingOperationInfo boi) {
        // allow an interceptor to override the SOAPAction if need be
        String action = (String) message.get(SoapBindingConstants.SOAP_ACTION);

        // Fall back on the SOAPAction in the operation info
        if (action == null) {
            if (boi == null) {
                action = "\"\"";
            } else {
                final BindingOperationInfo dboi = (BindingOperationInfo) boi.getProperty("dispatchToOperation");
                if (null != dboi) {
                    boi = dboi;
                }

                final SoapOperationInfo soi = boi.getExtensor(SoapOperationInfo.class);

                action = soi == null || StringUtils.isBlank(soi.getAction()) || StringUtils.isBlank(SOAPActionUriPrefix)
                        ? "\"\"" : (SOAPActionUriPrefix + soi.getAction());
            }
        }

        if (!action.startsWith("\"")) {
            action = new StringBuilder().append("\"").append(action).append("\"").toString();
        }

        return action;
    }
}
