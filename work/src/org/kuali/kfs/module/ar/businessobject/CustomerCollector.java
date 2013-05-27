/*
 * Copyright 2012 The Kuali Foundation.
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.ar.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * The class for storing the collector information for customers.
 */
public class CustomerCollector extends PersistableBusinessObjectBase implements ARCollector {

    private String principalId;
    private String customerNumber;

    private Person collector;
    private Customer customer;

    private final String userLookupRoleNamespaceCode = KFSConstants.OptionalModuleNamespaces.ACCOUNTS_RECEIVABLE;
    private final String userLookupRoleName = KFSConstants.SysKimApiConstants.ACCOUNTS_RECEIVABLE_COLLECTOR;

    /**
     * Default constructor
     */
    public CustomerCollector() {
        super();
    }

    /**
     * Gets the principalId.
     *
     * @see org.kuali.kfs.module.ar.businessobject.ARCollector#getPrincipalId()
     */

    public String getPrincipalId() {
        collector = SpringContext.getBean(org.kuali.rice.kim.api.identity.PersonService.class).updatePersonIfNecessary(principalId, collector);
        return principalId;
    }

    /**
     * Sets the principalId.
     *
     * @see org.kuali.kfs.module.ar.businessobject.ARCollector#setPrincipalId(java.lang.String)
     */
    @Override
    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }

    /**
     * Gets the customerNumber.
     *
     * @see org.kuali.kfs.module.ar.businessobject.ARCollector#getCustomerNumber()
     */
    @Override
    public String getCustomerNumber() {
        return customerNumber;
    }

    /**
     * Sets the customerNumber.
     *
     * @see org.kuali.kfs.module.ar.businessobject.ARCollector#setCustomerNumber(java.lang.String)
     */
    @Override
    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    /**
     * Gets the collector object.
     *
     * @see org.kuali.kfs.module.ar.businessobject.ARCollector#getCollector()
     */
    @Override
    public Person getCollector() {
        if (ObjectUtils.isNull(collector)) {
            collector = SpringContext.getBean(org.kuali.rice.kim.api.identity.PersonService.class).updatePersonIfNecessary(principalId, collector);
        }
        return collector;
    }

    /**
     * Sets the collector object.
     *
     * @see org.kuali.kfs.module.ar.businessobject.ARCollector#setCollector(org.kuali.rice.kim.api.identity.Person)
     */
    @Override
    public void setCollector(Person collector) {
        this.collector = collector;
    }

    /**
     * Gets the customer object.
     *
     * @return Returns customer object
     */
    public Customer getCustomer() {
        return customer;
    }

    /**
     * Sets the customer object.
     *
     * @param customer Customer object to set.
     */
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    /**
     * Gets the userLookupRoleNamespaceCode attribute.
     *
     * @return Returns userLookupRoleNamespaceCode attribute
     */
    public String getUserLookupRoleNamespaceCode() {
        return userLookupRoleNamespaceCode;
    }

    /**
     * Gets the userLookupRoleName attribute.
     *
     * @return Returns userLookupRoleName attribute.
     */
    public String getUserLookupRoleName() {
        return userLookupRoleName;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    @SuppressWarnings("unchecked")
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("principalId", this.principalId);
        m.put("customerNumber", this.customerNumber);
        return m;
    }

}
