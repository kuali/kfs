/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/cg/businessobject/ProjectDirector.java,v $
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

import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.bo.user.UserId;
import org.kuali.core.bo.user.UuId;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.util.SpringServiceLocator;

/**
 * 
 */
public class ProjectDirector extends BusinessObjectBase {

    private static final long serialVersionUID = -8864103362445919041L;
    private String personUniversalIdentifier;
    private UniversalUser universalUser;

    /**
     * Default no-arg constructor.
     */
    public ProjectDirector() {
        universalUser = new UniversalUser();
    }

    public UniversalUser getUniversalUser() {
        universalUser = SpringServiceLocator.getUniversalUserService().updateUniversalUserIfNecessary(personUniversalIdentifier, universalUser);
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
        m.put("universaliUser.getUniversalIdentifier", this.getPersonUniversalIdentifier());
        return m;
    }


}
