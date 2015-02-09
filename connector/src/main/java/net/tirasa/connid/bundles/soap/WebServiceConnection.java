/**
 * Copyright (C) ${project.inceptionYear} ConnId (connid-dev@googlegroups.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.tirasa.connid.bundles.soap;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import net.tirasa.connid.bundles.soap.cxf.ForceSoapActionOutInterceptor;
import net.tirasa.connid.bundles.soap.provisioning.interfaces.Provisioning;
import org.identityconnectors.common.logging.Log;

public class WebServiceConnection {

    /**
     * Logger definition.
     */
    private static final Log LOG = Log.getLog(WebServiceConnection.class);

    private static final String SUCCESS = "OK";

    private static Bus bus = null;

    private Provisioning provisioning;

    public WebServiceConnection(final WebServiceConfiguration configuration) {
        boolean isValidConf = false;
        try {
            configuration.validate();
            isValidConf = true;
        } catch (IllegalArgumentException e) {
            LOG.error(e, "Invalid configuration");
        }
        if (!isValidConf) {
            return;
        }

        Class<?> serviceClass = null;
        try {
            serviceClass = Class.forName(configuration.getServicename());
        } catch (ClassNotFoundException e) {
            LOG.error(e, "Provisioning class " + configuration.getServicename() + " not found");
        }
        if (serviceClass == null) {
            return;
        }

        synchronized (LOG) {
            if (bus == null) {
                bus = BusFactory.newInstance().createBus();
                BusFactory.setDefaultBus(bus);
                BusFactory.setThreadDefaultBus(bus);
            }
        }

        final JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setBus(bus);
        factory.setServiceClass(serviceClass);
        factory.setAddress(configuration.getEndpoint());

        provisioning = factory.create(Provisioning.class);

        try {
            final Client client = ClientProxy.getClient(provisioning);
            if (client != null) {
                final HTTPConduit conduit = (HTTPConduit) client.getConduit();
                final HTTPClientPolicy policy = conduit.getClient();
                policy.setConnectionTimeout(Long.parseLong(configuration.getConnectionTimeout()) * 1000L);
                policy.setReceiveTimeout(Long.parseLong(configuration.getReceiveTimeout()) * 1000L);

                client.getOutInterceptors().add(
                        new ForceSoapActionOutInterceptor(configuration.getSoapActionUriPrefix()));
            }
        } catch (Throwable t) {
            LOG.error(t, "Unknown exception");
        }
    }

    /**
     * Release internal resources.
     */
    public void dispose() {
        provisioning = null;
    }

    public static void shutdownBus() {
        synchronized (LOG) {
            if (bus != null) {
                bus.shutdown(true);
                BusFactory.clearDefaultBusForAnyThread(bus);
                bus = null;
            }
        }
    }

    /**
     * If internal connection is not usable, throw IllegalStateException.
     */
    public void test() {
        if (provisioning == null) {
            throw new IllegalStateException("Service port not found.");
        }

        final String res = provisioning.checkAlive();

        if (!SUCCESS.equals(res)) {
            throw new IllegalStateException("Invalid response.");
        }
    }

    public Provisioning getProvisioning() {
        return provisioning;
    }
}
