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
package org.kuali.module.kra.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.service.UniversalUserService;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.bo.ChartUser;
import org.kuali.module.chart.service.ChartUserService;

/**
 * This class represents an ad-hoc person.
 */
public class AdhocPerson extends AbstractAdhoc {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AdhocPerson.class);

    private String personUniversalIdentifier;
    private String personUserIdentifier;
    private String name;
    private UniversalUser user;

    public AdhocPerson() {
        super();
    }

    public AdhocPerson(String documentNumber, String personUniversalIdentifier) {
        this();
        this.setDocumentNumber(documentNumber);
        this.personUniversalIdentifier = personUniversalIdentifier;
    }

    /**
     * Gets the personUniversalIdentifier attribute.
     * 
     * @return Returns the personUniversalIdentifier.
     */
    public String getPersonUniversalIdentifier() {
        return personUniversalIdentifier;
    }

    /**
     * Sets the personUniversalIdentifier attribute value.
     * 
     * @param personUniversalIdentifier The personUniversalIdentifier to set.
     */
    public void setPersonUniversalIdentifier(String personUniversalIdentifier) {
        this.personUniversalIdentifier = personUniversalIdentifier;
    }

    /**
     * Gets the user attribute.
     * 
     * @return Returns the user.
     */
    public UniversalUser getUser() {
        user = SpringContext.getBean(UniversalUserService.class).updateUniversalUserIfNecessary(personUniversalIdentifier, user);
        return user;
    }

    /**
     * Sets the user attribute value.
     * 
     * @param user The user to set.
     * @deprecated Should not be set. User should be retrieved from SpringContext each time. See getUser() above.
     */
    public void setUser(UniversalUser user) {
        this.user = user;
    }
    
    public String getPrimaryDepartmentCode() {
        String org = "";
        if (user == null || user.getPersonUserIdentifier() == null) {
            user = null;
            try {
                user = SpringContext.getBean(UniversalUserService.class).getUniversalUser(getPersonUniversalIdentifier());
            }
            catch (UserNotFoundException ex) {
                // do nothing, leave user as null
            }
        }
        if (user == null) {
            return "";
        }
        if (this.user.getModuleUser(ChartUser.MODULE_ID) != null) {
            org = ((ChartUser) this.user.getModuleUser(ChartUser.MODULE_ID)).getOrganizationCode();
        }
        else {
            org = SpringContext.getBean(ChartUserService.class).getDefaultOrganizationCode(this.user);
        }
        return org;
    }

    /**
     * This method retrieves the associated user id from the UniversalUser attribute.
     * 
     * @return The user id of the associated user.
     */
    public String getPersonUserIdentifier() {
        if (user == null || user.getPersonUserIdentifier() == null) {
            user = null;
            try {
                user = SpringContext.getBean(UniversalUserService.class).getUniversalUser(getPersonUniversalIdentifier());
            }
            catch (UserNotFoundException ex) {
                // do nothing, leave user as null
            }
        }
        if (user == null) {
            return "";
        }
        return user.getPersonUserIdentifier();
    }

    /**
     * This method has no function and is only here to satisfy Struts.
     * 
     * @param userIdentifier User id to be passed in.
     */
    public void setPersonUserIdentifier(String userIdentifier) {
        // do nothing, the getter will handle this
    }

    /**
     * This method retrieves the associated user name from the UniversalUser attribute.
     * 
     * @return The user name in the format of LAST, FIRST
     */
    public String getName() {
        if (user == null || user.getPersonName() == null) {
            user = null;
            try {
                user = SpringContext.getBean(UniversalUserService.class).getUniversalUser(getPersonUniversalIdentifier());
            }
            catch (UserNotFoundException ex) {
                // do nothing, leave UU as null
            }
        }
        if (user == null) {
            return "";
        }
        return user.getPersonName();
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
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.getDocumentNumber());
        m.put("personUniversalIdentifier", this.personUniversalIdentifier);
        return m;
    }
}
