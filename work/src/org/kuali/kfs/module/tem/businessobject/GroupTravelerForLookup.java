/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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

    private String principalId;

    private String firstName;
    private String lastName;
    private String employeeId;
    private TemConstants.GroupTravelerType groupTravelerTypeCode;

    private boolean active;

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

    public TemConstants.GroupTravelerType getGroupTravelerTypeCode() {
        return this.groupTravelerTypeCode;
    }

    public void setGroupTravelerTypeCode(TemConstants.GroupTravelerType groupTravelerTypeCode) {
        this.groupTravelerTypeCode = groupTravelerTypeCode;
    }
}
