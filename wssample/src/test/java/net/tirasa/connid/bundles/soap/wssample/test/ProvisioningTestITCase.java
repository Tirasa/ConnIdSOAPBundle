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
package net.tirasa.connid.bundles.soap.wssample.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.tirasa.connid.bundles.soap.exceptions.ProvisioningException;
import net.tirasa.connid.bundles.soap.provisioning.interfaces.Provisioning;
import net.tirasa.connid.bundles.soap.to.WSAttributeValue;
import net.tirasa.connid.bundles.soap.to.WSChange;
import net.tirasa.connid.bundles.soap.to.WSUser;
import net.tirasa.connid.bundles.soap.utilities.Operand;
import net.tirasa.connid.bundles.soap.utilities.Operator;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:wssampleContext.xml" })
public class ProvisioningTestITCase {

    private static final Logger LOG = LoggerFactory.getLogger(ProvisioningTestITCase.class);

    @Autowired
    private JaxWsProxyFactoryBean proxyFactory;

    private Provisioning provisioning;

    @Before
    public void init() {
        provisioning = (Provisioning) proxyFactory.create();
    }

    @Test
    public void authenticate() {
        Throwable t = null;
        try {
            String uid = provisioning.authenticate("TESTUSER", "password");
            assertEquals("TESTUSER", uid);
        } catch (ProvisioningException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Unknown exception!", e);
            }
            t = e;
        }
        assertNull(t);
    }

    @Test
    public void checkAlive() {
        Throwable t = null;
        try {
            provisioning.checkAlive();
        } catch (Exception e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Unknown exception!", e);
            }
            t = e;
        }
        assertNull(t);
    }

    @Test
    public void schema() {
        Throwable t = null;
        try {
            provisioning.schema();
        } catch (Exception e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Unknown exception!", e);
            }
            t = e;
        }
        assertNull(t);
    }

    @Test
    public void create() {
        Throwable t = null;
        try {
            WSAttributeValue uid = new WSAttributeValue();
            uid.setName("userId");
            uid.addValue("john.doe@gmail.com");
            uid.setKey(true);

            WSAttributeValue password = new WSAttributeValue();
            password.setName("password");
            password.addValue("password");
            password.setPassword(true);

            WSAttributeValue type = new WSAttributeValue();
            type.setName("type");
            type.addValue("person");

            WSAttributeValue name = new WSAttributeValue();
            name.setName("name");
            name.addValue("john");

            WSAttributeValue surname = new WSAttributeValue();
            surname.setName("surname");
            surname.addValue("doe");

            WSAttributeValue fullname = new WSAttributeValue();
            fullname.setName("fullname");
            fullname.addValue("john doe");

            WSAttributeValue birthdate = new WSAttributeValue();
            birthdate.setName("birthdate");
            birthdate.addValue("01/01/1990");

            List<WSAttributeValue> attrs = new ArrayList<WSAttributeValue>();
            attrs.add(uid);
            attrs.add(password);
            attrs.add(type);
            attrs.add(name);
            attrs.add(surname);
            attrs.add(fullname);
            attrs.add(birthdate);

            String accountId = provisioning.create(attrs);

            assertNotNull(accountId);
            assertEquals(accountId, "john.doe@gmail.com");
        } catch (ProvisioningException e) {
            LOG.debug("Unknown exception!", e);
            t = e;
        }
        assertNull(t);
    }

    @Test
    public void update() {
        Throwable t = null;
        try {
            WSAttributeValue surname = new WSAttributeValue();
            surname.setName("surname");
            surname.addValue("verde");
            surname.setKey(true);

            WSAttributeValue name = new WSAttributeValue();
            name.setName("name");
            name.addValue("pino");

            List<WSAttributeValue> attrs = new ArrayList<WSAttributeValue>();
            attrs.add(surname);
            attrs.add(name);

            String uid = provisioning.update("test2", attrs);

            assertNotNull(uid);
            assertEquals("test2", uid);
        } catch (ProvisioningException e) {
            LOG.debug("Unknown exception!", e);
            t = e;
        }
        assertNull(t);
    }

    @Test
    public void delete() {
        Throwable t = null;
        try {
            provisioning.delete("test1");
        } catch (ProvisioningException e) {
            LOG.debug("Unknown exception!", e);
            t = e;
        }
        assertNull(t);
    }

    @Test
    public void query() {
        Throwable t = null;

        try {
            Operand op1 = new Operand(Operator.EQ, "name", "Pino");
            Operand op2 = new Operand(Operator.EQ, "surname", "Bianchi");
            Operand op3 = new Operand(Operator.EQ, "surname", "Rossi");

            Set<Operand> sop1 = new HashSet<Operand>();
            sop1.add(op1);
            sop1.add(op2);

            Set<Operand> sop2 = new HashSet<Operand>();
            sop2.add(op1);
            sop2.add(op3);

            Operand op4 = new Operand(Operator.AND, sop1);
            Operand op5 = new Operand(Operator.AND, sop2);

            Set<Operand> sop = new HashSet<Operand>();
            sop.add(op4);
            sop.add(op5);

            Operand query = new Operand(Operator.OR, sop, true);

            List<WSUser> results = provisioning.query(query);

            assertNotNull(results);
            assertFalse(results.isEmpty());
            for (WSUser user : results) {
                LOG.debug("User: " + user);
            }
        } catch (Exception e) {
            LOG.debug("Unknown exception!", e);
            t = e;
        }
        assertNull(t);
    }

    @Test
    public void resolveUser() {
        Throwable t = null;
        try {
            String uid = provisioning.resolve("test2");
            assertEquals("test2", uid);
        } catch (ProvisioningException e) {
            LOG.debug("Unknown exception!", e);
            t = e;
        }
        assertNull(t);
    }

    @Test
    public void resolveWrongUser() {
        Throwable t = null;
        try {
            String uid = provisioning.resolve("wrong");
            assertNull(uid);
        } catch (ProvisioningException e) {
            LOG.debug("Unknown exception!", e);
            t = e;
        }
        assertNull(t);
    }

    @Test
    public void resolveRole() {
        Throwable t = null;
        try {
            String uid = provisioning.resolve("roleOne");
            assertEquals("roleOne", uid);
        } catch (ProvisioningException e) {
            LOG.debug("Unknown exception!", e);
            t = e;
        }
        assertNull(t);
    }

    @Test
    public void resolveWrongRole() {
        Throwable t = null;
        try {
            String uid = provisioning.resolve("wrong");
            assertNull(uid);
        } catch (ProvisioningException e) {
            LOG.debug("Unknown exception!", e);
            t = e;
        }
        assertNull(t);
    }

    @Test
    public void getLatestChangeNumber() {
        Throwable t = null;
        try {
            int token = provisioning.getLatestChangeNumber();
            assertEquals(0, token);
        } catch (ProvisioningException e) {
            LOG.debug("Unknown exception!", e);
            t = e;
        }
        assertNull(t);
    }

    @Test
    public void sync() {
        Throwable t = null;

        try {
            List<WSChange> results = null;
            if (provisioning.isSyncSupported()) {
                results = provisioning.sync();
                assertNotNull(results);

                for (WSChange change : results) {
                    LOG.debug("Delta: " + change.getId());
                }
            }
        } catch (ProvisioningException e) {
            LOG.debug("Unknown exception!", e);
            t = e;
        }
        assertNull(t);
    }
}
