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

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.PropertyConstants;

/**
 * This class represents an ad-hoc person.
 * 
 * 
 */
public class AdhocPerson extends AbstractAdhoc {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AdhocPerson.class);

    private String personUniversalIdentifier;
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
        return user;
    }

    /**
     * Sets the user attribute value.
     * 
     * @param user The user to set.
     */
    public void setUser(UniversalUser user) {
        this.user = user;
    }
    
    /**
     * Gets the org code based on deptid.
     * 
     * @return Returns the user.
     */
    public String getOrgCode() {
        if (this.getUser() != null) {
            if ( !StringUtils.isEmpty( this.getUser().getPrimaryDepartmentCode() ) ) {
                return this.getUser().getPrimaryDepartmentCode();
            } else {
                return this.getUser().getCampusCode();
            }
        }
        return "";
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(PropertyConstants.DOCUMENT_NUMBER, this.getDocumentNumber());
        m.put("personUniversalIdentifier", this.personUniversalIdentifier);
        return m;
    }
}
