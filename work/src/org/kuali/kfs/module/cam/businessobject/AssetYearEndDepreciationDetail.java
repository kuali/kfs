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
import org.kuali.rice.krad.bo.GlobalBusinessObjectDetailBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */

public class AssetYearEndDepreciationDetail extends GlobalBusinessObjectDetailBase implements MutableInactivatable {
    private Integer universityFiscalYear;
    private Long capitalAssetNumber;
    private boolean active;
    private boolean processed;
    private Asset asset;
    private AssetYearEndDepreciation assetYearEndDepreciation;

    /**
     * Default constructor.
     */
    public AssetYearEndDepreciationDetail() {
        setActive(true);
    }

    /**
     * univeristy fiscal year 
     * @return universityFiscalYear
     */
    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    /**
     * Set the universityFiscalYear
     * @param universityFiscalYear
     */
    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }

    /**
     * Gets the capitalAssetNumber attribute.
     * 
     * @return Returns the capitalAssetNumber
     */
    public Long getCapitalAssetNumber() {
        return capitalAssetNumber;
    }

    /**
     * Sets the capitalAssetNumber attribute.
     * 
     * @param capitalAssetNumber The capitalAssetNumber to set.
     */
    public void setCapitalAssetNumber(Long capitalAssetNumber) {
        this.capitalAssetNumber = capitalAssetNumber;
    }

    /**
     * Is active
     * @return active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * set active status
     * @param active
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Is processed
     * @return processed
     */
    public boolean isProcessed() {
        return processed;
    }

    /**
     * set Processed
     * @param processed
     */
    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    /**
     * Gets the asset attribute.
     * 
     * @return Returns the asset.
     */
    public Asset getAsset() {
        return asset;
    }

    /**
     * Sets the asset attribute value.
     * 
     * @param asset The asset to set.
     * @deprecated
     */
    public void setAsset(Asset asset) {
        this.asset = asset;
    }


    /**
     * get asset YearEnd Depreciation
     * @return assetYearEndDepreciation
     */
    public AssetYearEndDepreciation getAssetYearEndDepreciation() {
        return assetYearEndDepreciation;
    }

    /**
     * Sets assetYearEndDepreciation 
     * @param assetYearEndDepreciation
     */
    public void setAssetYearEndDepreciation(AssetYearEndDepreciation assetYearEndDepreciation) {
        this.assetYearEndDepreciation = assetYearEndDepreciation;
    }


    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("universityFiscalYear", universityFiscalYear);
        if (capitalAssetNumber != null) {
            m.put("capitalAssetNumber", capitalAssetNumber.toString());
        }
        return m;
    }
}