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

package org.kuali.module.cg.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.Inactivateable;
import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.service.UniversalUserService;
import org.kuali.kfs.context.SpringContext;

/**
 * 
 */
public class ProjectDirector extends PersistableBusinessObjectBase implements Inactivateable {

    private static final long serialVersionUID = -8864103362445919041L;
    private String personUniversalIdentifier;
    private String personUserIdentifier; // secondary key from user input, not persisted but takes priority over primary key.
    private UniversalUser universalUser;
    private boolean active;

    /**
     * Default no-arg constructor.
     */
    public ProjectDirector() {
    }

    /**
     * @return the {@link UniversalUser} to which the project director refers.
     */
    public UniversalUser getUniversalUser() {
        // If personUserIdentifier is not set, then fall back to personUniversalIdentifier.
        if (personUserIdentifier == null) {
            universalUser = SpringContext.getBean(UniversalUserService.class, "universalUserService").updateUniversalUserIfNecessary(personUniversalIdentifier, universalUser);
        }
        return universalUser;
    }

    /**
     * Sets the personUniversal attribute.
     * 
     * @param personUniversal The personUniversal to set.
     * @deprecated
     */
    public void setUniversalUser(UniversalUser user) {
        this.universalUser = user;
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
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("universalUser.getUniversalIdentifier", this.getPersonUniversalIdentifier());
        return m;
    }

    /**
     * @return the person name
     */
    public String getPersonName() {
        UniversalUser u = getUniversalUser();
        return u == null ? "" : u.getPersonName();
    }

    /**
     * @param personName the person name.
     */
    public void setPersonName(String personName) {
        if ( universalUser == null ) {
            universalUser = new UniversalUser();
        }
        universalUser.setPersonName( personName );
    }

    /**
     * @return the persons first name
     */
    public String getPersonFirstName() {
        UniversalUser u = getUniversalUser();
        return u == null ? "" : u.getPersonFirstName();
    }

    /**
     * @param personFirstName the persons first name
     */
    public void setPersonFirstName(String personFirstName) {
        if ( universalUser == null ) {
            universalUser = new UniversalUser();
        }
        universalUser.setPersonFirstName( personFirstName );
    }

    /**
     * @return the persons last name
     */
    public String getPersonLastName() {
        UniversalUser u = getUniversalUser();
        return u == null ? "" : u.getPersonLastName();
    }

    /**
     * @param personLastName the persons last name
     */
    public void setPersonLastName(String personLastName) {
        if ( universalUser == null ) {
            universalUser = new UniversalUser();
        }
        universalUser.setPersonName( personLastName );
    }

    /**
     * @return the userID for the person.
     */
    public String getPersonUserIdentifier() {
        if (personUserIdentifier != null) {
            return personUserIdentifier;
        }
        UniversalUser u = getUniversalUser();
        return u == null ? "" : u.getPersonUserIdentifier();
    }

    /**
     * @param personUserIdentifier the userID for the person.
     */
    public void setPersonUserIdentifier(String personUserIdentifier) {
        this.personUserIdentifier = personUserIdentifier;
        if (universalUser == null || !personUserIdentifier.equals(universalUser.getPersonUserIdentifier())) {
            try {
                universalUser = SpringContext.getBean(UniversalUserService.class, "universalUserService").getUniversalUserByAuthenticationUserId( personUserIdentifier );
            } catch ( UserNotFoundException ex ) {
                universalUser = null;
            }
        }
    }

    /**
     * @see org.kuali.core.bo.Inactivateable#isActive()
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @see org.kuali.core.bo.Inactivateable#setActive(boolean)
     */
    public void setActive(boolean active) {
        this.active = active;
    }
    
}
