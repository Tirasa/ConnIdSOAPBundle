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
package net.tirasa.connid.bundles.soap.provisioning.interfaces;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.xml.ws.BindingType;
import jakarta.xml.ws.soap.SOAPBinding;
import java.util.List;
import net.tirasa.connid.bundles.soap.exceptions.ProvisioningException;
import net.tirasa.connid.bundles.soap.to.WSAttribute;
import net.tirasa.connid.bundles.soap.to.WSAttributeValue;
import net.tirasa.connid.bundles.soap.to.WSChange;
import net.tirasa.connid.bundles.soap.to.WSUser;
import net.tirasa.connid.bundles.soap.utilities.Operand;

@WebService
@BindingType(SOAPBinding.SOAP12HTTP_BINDING)
public interface Provisioning {

    /**
     * Checks if authentication is supported.
     *
     * @return true if the resource support authentication.
     */
    @WebMethod(operationName = "isAuthenticationSupported", action = "isAuthenticationSupported")
    Boolean isAuthenticationSupported();

    /**
     * Checks if synchronization is supported.
     *
     * @return true if the resource support synchronization.
     */
    @WebMethod(operationName = "isSyncSupported", action = "isSyncSupported")
    Boolean isSyncSupported();

    /**
     * Verify user creentials
     *
     * @param username
     * @param password
     * @return the accountid of the first account that match username and password.
     * @throws ProvisioningException in case of authentication failed.
     */
    @WebMethod(operationName = "authenticate", action = "authenticate")
    String authenticate(
            @WebParam(name = "username") String username,
            @WebParam(name = "password") String password)
            throws ProvisioningException;

    /**
     * Returns "OK" if the resource is available.
     *
     * @return the string "OK" in case of availability of the resource.
     */
    @WebMethod(operationName = "checkAlive", action = "checkAlive")
    String checkAlive();

    /**
     * Returns the schema.
     *
     * @return a set of attributes.
     */
    @WebMethod(operationName = "schema", action = "schema")
    List<WSAttribute> schema();

    /**
     * Creates user account.
     *
     * @param data set of account attributes.
     * @return accountid of the account created.
     * @throws ProvisioningException in case of failure.
     */
    @WebMethod(operationName = "create", action = "create")
    String create(@WebParam(name = "data") List<WSAttributeValue> data)
            throws ProvisioningException;

    /**
     * Updates user account.
     *
     * @param accountid.
     * @param data set of attributes to be updated.
     * @return accountid.
     * @throws ProvisioningException in case of failure
     */
    @WebMethod(operationName = "update", action = "update")
    String update(
            @WebParam(name = "accountid") String accountid,
            @WebParam(name = "data") List<WSAttributeValue> data)
            throws ProvisioningException;

    /**
     * Deletes user account.
     *
     * @param accountid.
     * @return accountid.
     * @throws ProvisioningException in case of failure.
     */
    @WebMethod(operationName = "delete", action = "delete")
    String delete(@WebParam(name = "accountid") String accountid)
            throws ProvisioningException;

    /**
     * Searches for user accounts.
     *
     * @param query filter
     * @return a set of user accounts.
     */
    @WebMethod(operationName = "query", action = "query")
    List<WSUser> query(@WebParam(name = "query") Operand query);

    /**
     * Returns accountid related to the specified username.
     *
     * @param username.
     * @return accountid or null if username not found
     * @throws ProvisioningException in case of failure.
     */
    @WebMethod(operationName = "resolve", action = "resolve")
    String resolve(@WebParam(name = "username") String username)
            throws ProvisioningException;

    /**
     * Gets the latest change id.
     *
     * @return change id.
     * @throws ProvisioningException in case of failure.
     */
    @WebMethod(operationName = "getLatestChangeNumber", action = "getLatestChangeNumber")
    int getLatestChangeNumber()
            throws ProvisioningException;

    /**
     * Returns changes to be synchronized.
     *
     * @return a set of changes
     * @throws ProvisioningException in case of failure
     */
    @WebMethod(operationName = "sync", action = "sync")
    List<WSChange> sync()
            throws ProvisioningException;
}
