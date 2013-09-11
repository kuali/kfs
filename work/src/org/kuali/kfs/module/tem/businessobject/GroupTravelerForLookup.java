/*
 * Copyright 2013 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.businessobject;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.ar.AccountsReceivableCustomer;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.bo.TransientBusinessObjectBase;

public class GroupTravelerForLookup extends TransientBusinessObjectBase implements MutableInactivatable {
    private String customerNumber;
    private String customerName;

    private String principalName;
    private String firstName;
    private String lastName;
    private String employeeId;

    private boolean active;

    private String principalId;
    private String name;

    private AccountsReceivableCustomer customer;
    private Person person;

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getPrincipalName() {
        return principalName;
    }

    public void setPrincipalName(String principalName) {
        this.principalName = principalName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String personFirstName) {
        this.firstName = personFirstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String personLastName) {
        this.lastName = personLastName;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getPrincipalId() {
        return principalId;
    }

    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }

    @Override
    public boolean isActive() {
        return this.active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @return the id which generally represents this group traveler
     */
    public String getGroupTravelerId() {
        if (!StringUtils.isBlank(principalId)) {
            return principalId;
        }
        if (!StringUtils.isBlank(customerNumber)) {
            return customerNumber;
        }
        return null;
    }

    /**
     * @return the name of this group traveler
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name on this GroupTravelerForLookup
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the related Customer, if one exists
     */
    public AccountsReceivableCustomer getCustomer() {
        return customer;
    }

    public void setCustomer(AccountsReceivableCustomer customer) {
        this.customer = customer;
    }

    /**
     * @return the related Person, if one exists
     */
    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    /**
     * Determines the traveler type code - if we've got a principal id, we're likely an employee; if we've got a customer number, we've probably got an non employee; and if we don't have either, then who knows?  Only the fox, my friend.  Only the fox knows.
     * @return the traveler type code for this potential group traveler
     */
    public String getTravelerTypeCode() {
        if (!StringUtils.isBlank(principalId)) {
            return TemConstants.EMP_TRAVELER_TYP_CD;
        }
        if (!StringUtils.isBlank(customerNumber)) {
            return TemConstants.NONEMP_TRAVELER_TYP_CD;
        }
        return null;
    }
}
