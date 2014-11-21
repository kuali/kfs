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
package org.kuali.kfs.module.cam.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class AssetAcquisitionType extends PersistableBusinessObjectBase implements MutableInactivatable {

    private String acquisitionTypeCode;
    private String acquisitionTypeName;
    private String incomeAssetObjectCode;
    private boolean active;

    /**
     * Default constructor.
     */
    public AssetAcquisitionType() {
    }

    /**
     * Gets the acquisitionTypeCode attribute.
     * 
     * @return Returns the acquisitionTypeCode
     */
    public String getAcquisitionTypeCode() {
        return acquisitionTypeCode;
    }

    /**
     * Sets the acquisitionTypeCode attribute.
     * 
     * @param acquisitionTypeCode The acquisitionTypeCode to set.
     */
    public void setAcquisitionTypeCode(String acquisitionTypeCode) {
        this.acquisitionTypeCode = acquisitionTypeCode;
    }

    /**
     * Gets the acquisitionTypeName attribute.
     * 
     * @return Returns the acquisitionTypeName
     */
    public String getAcquisitionTypeName() {
        return acquisitionTypeName;
    }

    /**
     * Sets the acquisitionTypeName attribute.
     * 
     * @param acquisitionTypeName The acquisitionTypeName to set.
     */
    public void setAcquisitionTypeName(String acquisitionTypeName) {
        this.acquisitionTypeName = acquisitionTypeName;
    }

    /**
     * Gets the incomeAssetObjectCode attribute.
     * 
     * @return Returns the incomeAssetObjectCode
     */
    public String getIncomeAssetObjectCode() {
        return incomeAssetObjectCode;
    }

    /**
     * Sets the incomeAssetObjectCode attribute.
     * 
     * @param incomeAssetObjectCode The incomeAssetObjectCode to set.
     */
    public void setIncomeAssetObjectCode(String incomeAssetObjectCode) {
        this.incomeAssetObjectCode = incomeAssetObjectCode;
    }

    /**
     * Gets the active attribute.
     * 
     * @return Returns the active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute.
     * 
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("acquisitionTypeCode", this.acquisitionTypeCode);
        return m;
    }
}
