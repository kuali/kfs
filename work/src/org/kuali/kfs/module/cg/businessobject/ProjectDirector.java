/*
 * Copyright 2005-2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kuali.kfs.module.cg.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.bo.Inactivateable;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * 
 */
public class ProjectDirector extends PersistableBusinessObjectBase implements Inactivateable {

    private static final long serialVersionUID = -8864103362445919041L;
    private String principalId;
    private String principalName; // secondary key from user input, not persisted but takes priority over primary key.
    private Person person;
    private boolean active;
    // These four field were added after the Person was converted to Person.
    // Person has no setters so we are now storing them locally and deferring to
    // the person when possible, using the instances one when person is unavailable.
    private String personName = "";
    private String personFirstName = "";
    private String personLastName = "";
    private String primaryDepartmentCode = "";

    /**
     * Default no-arg constructor.
     */
    public ProjectDirector() {
    }

    /**
     * @return the {@link Person} to which the project director refers.
     */
    public Person getPerson() {
        // If principalName is not set, then fall back to principalId.
        if (principalName == null) {
            person = SpringContext.getBean(org.kuali.rice.kim.service.PersonService.class).updatePersonIfNecessary(principalId, person);
        }
        return person;
    }

    /**
     * Sets the personUniversal attribute.
     * 
     * @param personUniversal The personUniversal to set.
     * @deprecated
     */
    public void setPerson(Person user) {
        this.person = user;
    }

    /**
     * Gets the principalId attribute.
     * 
     * @return Returns the principalId.
     */
    public String getPrincipalId() {
        return principalId;
    }

    /**
     * Sets the principalId attribute value.
     * 
     * @param principalId The principalId to set.
     */
    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }

    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("person.getUniversalIdentifier", this.getPrincipalId());
        return m;
    }

    /**
     * @return the person name
     */
    public String getName() {
        Person u = getPerson();
        return u == null ? personName : u.getName();
    }

    /**
     * @param personName the person name.
     */
    public void setName(String personName) {
        this.personName = personName;
    }

    /**
     * @return the persons first name
     */
    public String getFirstName() {
        Person u = getPerson();
        return u == null ? personFirstName : u.getFirstName();
    }

    /**
     * @param personFirstName the persons first name
     */
    public void setFirstName(String personFirstName) {
        this.personFirstName = personFirstName;
    }

    /**
     * @return the persons last name
     */
    public String getLastName() {
        Person u = getPerson();
        return u == null ? personLastName : u.getLastName();
    }

    /**
     * @param personLastName the persons last name
     */
    public void setLastName(String personLastName) {
        this.personLastName = personLastName;
    }

    /**
     * @return the userID for the person.
     */
    public String getPrincipalName() {
        if (principalName != null) {
            return principalName;
        }
        Person u = getPerson();
        return u == null ? principalName : u.getPrincipalName();
    }

    /**
     * @param principalName the userID for the person.
     */
    public void setPrincipalName(String principalName) {
        this.principalName = principalName;
        if (principalName == null) {
            person = null;
        }
        else if (ObjectUtils.isNull(person) || !principalName.equals(person.getPrincipalName())) {
            person = SpringContext.getBean(org.kuali.rice.kim.service.PersonService.class).getPersonByPrincipalName(principalName);
        }
    }


    /**
     * @return the primary DepartmentCodee
     */
    public String getPrimaryDepartmentCode() {
        Person u = getPerson();
        return u == null ? primaryDepartmentCode : u.getPrimaryDepartmentCode();
    }

    /**
     * @param personName the primary DepartmentCode.
     */
    public void setPrimaryDepartmentCode(String primaryDepartmentCode) {
        this.primaryDepartmentCode = primaryDepartmentCode;
    }


    /**
     * @see org.kuali.rice.kns.bo.Inactivateable#isActive()
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @see org.kuali.rice.kns.bo.Inactivateable#setActive(boolean)
     */
    public void setActive(boolean active) {
        this.active = active;
    }

}


