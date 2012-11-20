/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.identity;

import org.kuali.kfs.sys.identity.KfsKimAttributes;

public class TemKimAttributes extends KfsKimAttributes {

    public static final String PROFILE_PRINCIPAL_ID = "profilePrincipalId";
    
    private Integer profilePrincipalId;

    /**
     * Gets the profilePrincipalId attribute. 
     * @return Returns the profilePrincipalId.
     */
    public Integer getProfilePrincipalId() {
        return profilePrincipalId;
    }

    /**
     * Sets the profilePrincipalId attribute value.
     * @param profilePrincipalId The profilePrincipalId to set.
     */
    public void setProfilePrincipalId(Integer profilePrincipalId) {
        this.profilePrincipalId = profilePrincipalId;
    }

}
