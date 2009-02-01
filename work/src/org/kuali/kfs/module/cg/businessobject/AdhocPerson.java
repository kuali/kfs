/*
 * Copyright 2007 The Kuali Foundation.
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

import org.kuali.kfs.module.cg.CGConstants;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.ChartOrgHolder;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.FinancialSystemUserService;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kns.util.GlobalVariables;

/**
 * This class represents an ad-hoc person.
 */
public class AdhocPerson extends AbstractAdhoc {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AdhocPerson.class);

    private String principalId;
    private String principalName;
    private String name;
    private Person user;
    private transient PersonService personService;
    
    public AdhocPerson() {
        super();
    }

    public AdhocPerson(String documentNumber, String principalId) {
        this();
        this.setDocumentNumber(documentNumber);
        this.principalId = principalId;
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
     * Gets the user attribute.
     * 
     * @return Returns the user.
     */
    public Person getUser() {
        user = getKfsUserService().updatePersonIfNecessary(principalId, user);
        return user;
    }

    /**
     * Sets the user attribute value.
     * 
     * @param user The user to set.
     * @deprecated Should not be set. User should be retrieved from SpringContext each time. See getUser() above.
     */
    public void setUser(Person user) {
        this.user = user;
    }
    
    public String getPrimaryDepartmentCode() {
        String org = "";
        if (user == null || user.getPrincipalName() == null) {
            user = getKfsUserService().getPerson(getPrincipalId());
        }
        if (user == null) {
            return "";
        }
        org = SpringContext.getBean(FinancialSystemUserService.class).getPrimaryOrganization(user, CGConstants.CG_NAMESPACE_CODE).getOrganizationCode();
        return org;
    }

    /**
     * This method retrieves the associated user id from the Person attribute.
     * 
     * @return The user id of the associated user.
     */
    public String getPrincipalName() {
        if (user == null || user.getPrincipalName() == null) {
            user = getKfsUserService().getPerson(getPrincipalId());
        }
        if (user == null) {
            return "";
        }
        return user.getPrincipalName();
    }

    /**
     * This method has no function and is only here to satisfy Struts.
     * 
     * @param userIdentifier User id to be passed in.
     */
    public void setPrincipalName(String userIdentifier) {
        // do nothing, the getter will handle this
    }

    /**
     * This method retrieves the associated user name from the Person attribute.
     * 
     * @return The user name in the format of LAST, FIRST
     */
    public String getName() {
        if (user == null || user.getName() == null) {
            user = getKfsUserService().getPerson(getPrincipalId());
        }
        if (user == null) {
            return "";
        }
        return user.getName();
    }

    /**
     * This method has no function and is only here to satisfy Struts.
     * 
     * @param name The name of the user.
     */
    public void setName(String name) {
        // do nothing, the getter will handle this
    }

    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.getDocumentNumber());
        m.put("principalId", this.principalId);
        return m;
    }

    public PersonService getKfsUserService() {
        if ( personService == null ) {
            personService = SpringContext.getBean(PersonService.class);
        }
        return personService;
    }
}

