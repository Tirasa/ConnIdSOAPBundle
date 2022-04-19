/**
 * Copyright (C) 2011 ConnId (connid-dev@googlegroups.com)
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
package net.tirasa.connid.bundles.soap.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.util.AssertionErrors.fail;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import org.identityconnectors.common.IOUtil;
import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.api.APIConfiguration;
import org.identityconnectors.framework.api.ConfigurationProperties;
import org.identityconnectors.framework.api.ConfigurationProperty;
import org.identityconnectors.framework.api.ConnectorFacade;
import org.identityconnectors.framework.api.ConnectorFacadeFactory;
import org.identityconnectors.framework.api.ConnectorInfo;
import org.identityconnectors.framework.api.ConnectorInfoManager;
import org.identityconnectors.framework.api.ConnectorInfoManagerFactory;
import org.identityconnectors.framework.api.ConnectorKey;
import org.identityconnectors.framework.api.operations.APIOperation;
import org.identityconnectors.framework.api.operations.CreateApiOp;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeBuilder;
import org.identityconnectors.framework.common.objects.AttributeInfo;
import org.identityconnectors.framework.common.objects.ConnectorObject;
import org.identityconnectors.framework.common.objects.Name;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.ObjectClassInfo;
import org.identityconnectors.framework.common.objects.ResultsHandler;
import org.identityconnectors.framework.common.objects.Schema;
import org.identityconnectors.framework.common.objects.Uid;
import org.identityconnectors.framework.common.objects.filter.Filter;
import org.identityconnectors.framework.common.objects.filter.FilterBuilder;
import net.tirasa.connid.bundles.soap.WebServiceConnector;
import net.tirasa.connid.bundles.soap.provisioning.interfaces.Provisioning;
import org.identityconnectors.common.logging.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SOAPTestITCase {

    private static final Log LOG = Log.getLog(SOAPTestITCase.class);

    private static final String TESTUSER = "TESTUSER";

    private static final String ENDPOINT_PREFIX = "http://localhost:8888/wssample/services";

    private static final String SERVICE = "/provisioning";

    private static final String BUNDLE_NAME = "net.tirasa.connid.bundles.soap";

    private static final String BUNDLE_CLASS = WebServiceConnector.class.getName();

    private String bundleversion = null;

    private String bundledirectory;

    private ConnectorFacade connector;

    /**
     * Uses the ConnectorInfoManager to retrieve a ConnectorInfo object for the connector.
     */
    @BeforeEach
    public void init() {
        final ResourceBundle resourceBundle = ResourceBundle.getBundle("bundle");
        bundleversion = resourceBundle.getString("bundleversion");
        bundledirectory = resourceBundle.getString("bundledirectory");
        assertNotNull(bundleversion);
        assertNotNull(bundledirectory);

        final File bundleDirectory = new File(bundledirectory);
        final List<URL> urls = new ArrayList<>();
        for (String filename : bundleDirectory.list((file, name) -> name.endsWith("-bundle.jar"))) {
            try {
                urls.add(IOUtil.makeURL(bundleDirectory, filename));
            } catch (IOException ignore) {
                // ignore exception and don't add bundle
                LOG.warn(ignore, "\"" + bundleDirectory.toString() + "/" + filename + "\""
                        + " is not a valid connector bundle.");
            }
        }
        assertFalse(urls.isEmpty());
        LOG.ok("URL: " + urls.toString());

        final ConnectorInfoManagerFactory connectorInfoManagerFactory = ConnectorInfoManagerFactory.getInstance();
        final ConnectorInfoManager manager =
                connectorInfoManagerFactory.getLocalManager(urls.toArray(new URL[urls.size()]));
        assertNotNull(manager);

        // list connectors info
        final List<ConnectorInfo> infos = manager.getConnectorInfos();
        assertNotNull(infos);
        LOG.ok("infos size: " + infos.size());

        for (ConnectorInfo i : infos) {
            LOG.ok("Name: " + i.getConnectorDisplayName());
        }

        LOG.ok("\nBundle name: " + BUNDLE_NAME
                + "\nBundle version: " + bundleversion
                + "\nBundle class: " + BUNDLE_CLASS);

        // specify a connector
        final ConnectorKey key = new ConnectorKey(BUNDLE_NAME, bundleversion, BUNDLE_CLASS);
        assertNotNull(key);

        // get the specified connector.
        final ConnectorInfo info = manager.findConnectorInfo(key);
        assertNotNull(info);

        // create default configuration
        final APIConfiguration apiConfig = info.createDefaultAPIConfiguration();
        assertNotNull(apiConfig);

        // retrieve the ConfigurationProperties.
        final ConfigurationProperties properties = apiConfig.getConfigurationProperties();
        assertNotNull(properties);

        // Print out what the properties are (not necessary)
        if (LOG.isOk()) {
            for (String propName : properties.getPropertyNames()) {
                ConfigurationProperty prop = properties.getProperty(propName);

                LOG.ok("Property Name: " + prop.getName()
                        + "\nProperty Type: " + prop.getType());
            }
        }

        // Set all of the ConfigurationProperties needed by the connector.
        properties.setPropertyValue("endpoint", ENDPOINT_PREFIX + SERVICE);
        properties.setPropertyValue("servicename", Provisioning.class.getName());

        // Use the ConnectorFacadeFactory's newInstance() method to get a new connector.
        connector = ConnectorFacadeFactory.getInstance().newInstance(apiConfig);
        assertNotNull(connector);

        // Make sure we have set up the Configuration properly
        try {
            connector.validate();
            connector.test();
        } catch (RuntimeException e) {
            fail(e.getMessage());
        }
    }

    /**
     * Checks if a particular operation is supported.
     */
    @Test
    public void checkForOperation() {
        final Set<Class<? extends APIOperation>> ops = connector.getSupportedOperations();

        // check to see if the set contains the operation you care about
        assertTrue(ops.contains(CreateApiOp.class));
    }

    /**
     * Gets schema from the target resource.
     */
    @Test
    public void schema() {
        final Schema schema = connector.schema();
        assertNotNull(schema);

        final Set<ObjectClassInfo> ocis = schema.getObjectClassInfo();
        assertNotNull(ocis);

        for (ObjectClassInfo oci : ocis) {
            final Set<AttributeInfo> attrs = oci.getAttributeInfo();
            assertNotNull(attrs);

            if (LOG.isOk()) {
                for (AttributeInfo attr : attrs) {
                    LOG.ok("Attribute name: " + attr.getName()
                            + "\nAttribute type: " + attr.getType().getName());
                }
            }
        }
    }

    /**
     * Seraches for user accounts.
     */
    @Test
    public void search() {
        final List<ConnectorObject> results = new ArrayList<>();

        ResultsHandler resultsHandler = obj -> {
            results.add(obj);
            return true;
        };

        final Filter usernameFilter = FilterBuilder.startsWith(AttributeBuilder.build("USERID", "test"));

        final Filter nameFilter = FilterBuilder.equalTo(AttributeBuilder.build("NAME", "jhon"));

        final Filter surnameFilter = FilterBuilder.equalTo(AttributeBuilder.build("SURNAME", "doe"));

        final Filter filter = FilterBuilder.or(usernameFilter, FilterBuilder.and(nameFilter, surnameFilter));

        connector.search(ObjectClass.ACCOUNT, filter, resultsHandler, null);

        /**
         * Pay attention: results will be returned according to the filter above.
         */
        assertFalse(results.isEmpty());

        if (LOG.isOk()) {
            for (ConnectorObject obj : results) {
                LOG.ok("Name: " + obj.getName().getNameValue()
                        + "\nUID: " + obj.getUid().getUidValue());
            }
        }
    }

    /**
     * Creates user account.
     */
    @Test
    public void create() {
        final Set<Attribute> attrs = new HashSet<>();
        attrs.add(new Name(TESTUSER));

        attrs.add(AttributeBuilder.buildPassword("TESTPASSWORD".toCharArray()));

        attrs.add(AttributeBuilder.build("name", "John"));
        attrs.add(AttributeBuilder.build("surname", "Doe"));
        attrs.add(AttributeBuilder.build("fullname", "John Doe"));
        attrs.add(AttributeBuilder.build("type", "person"));
        attrs.add(AttributeBuilder.build("birthdate", "12/03/1990"));

        final Uid userUid = connector.create(ObjectClass.ACCOUNT, attrs, null);

        assertNotNull(userUid);
        assertEquals(TESTUSER, userUid.getUidValue());
    }

    /**
     * Updates user account.
     */
    @Test
    public void update() {
        final Set<Attribute> attrs = new HashSet<>();
        attrs.add(new Name(TESTUSER));

        attrs.add(AttributeBuilder.buildPassword("NEWPASSWORD".toCharArray()));

        final Uid userUid = connector.update(ObjectClass.ACCOUNT, new Uid(TESTUSER), attrs, null);

        assertNotNull(userUid);
        assertEquals(TESTUSER, userUid.getUidValue());
    }

    /**
     * Deletes user account.
     */
    @Test
    public void delete() {
        final Uid userUid = connector.authenticate(
                ObjectClass.ACCOUNT, TESTUSER, new GuardedString("TESTPASSWORD".toCharArray()), null);
        assertNotNull(userUid);

        connector.delete(ObjectClass.ACCOUNT, userUid, null);
    }
}
