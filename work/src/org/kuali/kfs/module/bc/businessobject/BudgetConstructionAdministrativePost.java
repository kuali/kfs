/*
 * Copyright 2006 The Kuali Foundation
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

package org.kuali.kfs.module.bc.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * 
 */
public class BudgetConstructionAdministrativePost extends PersistableBusinessObjectBase {

    private String emplid;
    private String positionNumber;
    private String administrativePost;

    /**
     * Default constructor.
     */
    public BudgetConstructionAdministrativePost() {

    }

    /**
     * Gets the emplid attribute.
     * 
     * @return Returns the emplid
     */
    public String getEmplid() {
        return emplid;
    }

    /**
     * Sets the emplid attribute.
     * 
     * @param emplid The emplid to set.
     */
    public void setEmplid(String emplid) {
        this.emplid = emplid;
    }


    /**
     * Gets the positionNumber attribute.
     * 
     * @return Returns the positionNumber
     */
    public String getPositionNumber() {
        return positionNumber;
    }

    /**
     * Sets the positionNumber attribute.
     * 
     * @param positionNumber The positionNumber to set.
     */
    public void setPositionNumber(String positionNumber) {
        this.positionNumber = positionNumber;
    }


    /**
     * Gets the administrativePost attribute.
     * 
     * @return Returns the administrativePost
     */
    public String getAdministrativePost() {
        return administrativePost;
    }

    /**
     * Sets the administrativePost attribute.
     * 
     * @param administrativePost The administrativePost to set.
     */
    public void setAdministrativePost(String administrativePost) {
        this.administrativePost = administrativePost;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("emplid", this.emplid);
        m.put("positionNumber", this.positionNumber);
        return m;
    }

}
