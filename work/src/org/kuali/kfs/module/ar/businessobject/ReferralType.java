/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * This class is used for Referral Type
 * @author jignasha.
 */
public class ReferralType extends PersistableBusinessObjectBase {

    private String referralTypeCode;
    private String description;
    private boolean active;
    private boolean outsideCollectionAgencyInd;

    /**
     * Default constructor
     */
    public ReferralType() {
        super();
    }

    /**
     * Gets the active attribute.
     *
     * @return Returns the active.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute value.
     *
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }


    /**
     * Gets the referralTypeCode attribute.
     *
     * @return Returns the referralTypeCode.
     */
    public String getReferralTypeCode() {
        return referralTypeCode;
    }

    /**
     * Sets the referralTypeCode attribute value.
     *
     * @param referralTypeCode The referralTypeCode to set.
     */
    public void setReferralTypeCode(String referralTypeCode) {
        this.referralTypeCode = referralTypeCode;
    }

    /**
     * Gets the description attribute.
     *
     * @return Returns the description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description attribute value.
     *
     * @param description The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the outsideCollectionAgencyInd attribute.
     *
     * @return Returns the outsideCollectionAgencyInd.
     */
    public boolean isOutsideCollectionAgencyInd() {
        return outsideCollectionAgencyInd;
    }

    /**
     * Sets the outsideCollectionAgencyInd attribute.
     *
     * @param outsideCollectionAgencyInd The outsideCollectionAgencyInd to set.
     */
    public void setOutsideCollectionAgencyInd(boolean outsideCollectionAgencyInd) {
        this.outsideCollectionAgencyInd = outsideCollectionAgencyInd;
    }

    /**
     *
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */

    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("referralTypeCode", this.referralTypeCode);
        m.put("description", this.description);
        m.put("active", this.active);
        return m;
    }

}
