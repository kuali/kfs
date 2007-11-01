/*
 * Copyright 2006-2007 The Kuali Foundation.
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

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * 
 */
public class GrantDescription extends PersistableBusinessObjectBase {

    private String grantDescriptionCode;
    private String grantDescription;
    private boolean grantDescriptionActiveCode;

    /**
     * Default constructor.
     */
    public GrantDescription() {
    }

    /**
     * Gets the grantDescriptionCode attribute.
     * 
     * @return Returns the grantDescriptionCode
     */
    public String getGrantDescriptionCode() {
        return grantDescriptionCode;
    }

    /**
     * Sets the grantDescriptionCode attribute.
     * 
     * @param grantDescriptionCode The grantDescriptionCode to set.
     */
    public void setGrantDescriptionCode(String grantDescriptionCode) {
        this.grantDescriptionCode = grantDescriptionCode;
    }


    /**
     * Gets the grantDescription attribute.
     * 
     * @return Returns the grantDescription
     */
    public String getGrantDescription() {
        return grantDescription;
    }

    /**
     * Sets the grantDescription attribute.
     * 
     * @param grantDescription The grantDescription to set.
     */
    public void setGrantDescription(String grantDescription) {
        this.grantDescription = grantDescription;
    }


    /**
     * Gets the grantDescriptionActiveCode attribute.
     * 
     * @return Returns the grantDescriptionActiveCode
     */
    public boolean getGrantDescriptionActiveCode() {
        return grantDescriptionActiveCode;
    }

    /**
     * Sets the grantDescriptionActiveCode attribute.
     * 
     * @param grantDescriptionActiveCode The grantDescriptionActiveCode to set.
     */
    public void setGrantDescriptionActiveCode(boolean grantDescriptionActiveCode) {
        this.grantDescriptionActiveCode = grantDescriptionActiveCode;
    }

    /**
     * This method returns a boolean identifying whether or not this grant description is active or inactive.
     * 
     * @return True if the grant description is active, false if its inactive.
     */
    public boolean isRowActiveIndicator() {
        return grantDescriptionActiveCode;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("grantDescriptionCode", this.grantDescriptionCode);
        return m;
    }
}
