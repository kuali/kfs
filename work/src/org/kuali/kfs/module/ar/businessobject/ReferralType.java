/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.ar.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * This class is used for Referral Type
 */
public class ReferralType extends PersistableBusinessObjectBase implements MutableInactivatable {

    private String referralTypeCode;
    private String description;
    private boolean active;
    private boolean outsideCollectionAgency;

    /**
     * Gets the active attribute.
     *
     * @return Returns the active.
     */
    @Override
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute value.
     *
     * @param active The active to set.
     */
    @Override
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
     * Gets the outsideCollectionAgency attribute.
     *
     * @return Returns the outsideCollectionAgency.
     */
    public boolean isOutsideCollectionAgency() {
        return outsideCollectionAgency;
    }

    /**
     * Sets the outsideCollectionAgency attribute.
     *
     * @param outsideCollectionAgency The outsideCollectionAgency to set.
     */
    public void setOutsideCollectionAgency(boolean outsideCollectionAgency) {
        this.outsideCollectionAgency = outsideCollectionAgency;
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
