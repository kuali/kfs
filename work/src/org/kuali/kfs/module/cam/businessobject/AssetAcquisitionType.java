/*
 * Copyright 2008-2009 The Kuali Foundation
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
