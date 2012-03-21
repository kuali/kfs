/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.cam.document.service;

import java.util.Date;
import java.util.List;

import org.kuali.kfs.module.cam.businessobject.AssetGlobal;
import org.kuali.kfs.module.cam.document.gl.CamsGeneralLedgerPendingEntrySourceBase;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObject;


/**
 * The interface defines methods for Asset Document
 */
public interface AssetGlobalService {
    /**
     * To calculate the total payment amounts for each asset. lastEntry is used to handle decimal rounding. When calculate the
     * entries except the last one, divide total by number of assets. When calculate the lastEntry, first sum asset total amount for
     * assets except the last one, then subtract the sum from total.
     * 
     * @param assetGlobal
     * @param lastEntry
     * @return
     */
    public KualiDecimal totalPaymentByAsset(AssetGlobal assetGlobal, boolean lastEntry);

    /**
     * This method checks if member exists in the given group.
     * 
     * @param groupName
     * @param memberName
     * @return
     */
    public boolean existsInGroup(String groupName, String memberName);

    /**
     * Creates GL Postables
     */
    public void createGLPostables(AssetGlobal assetGlobal, CamsGeneralLedgerPendingEntrySourceBase assetGlobalGlPoster);

    /**
     * Validates if the document type matches that of Asset Separate.
     * 
     * @param assetGlobal
     * @return boolean
     */
    public boolean isAssetSeparate(AssetGlobal assetGlobal);

    /**
     * Validates if the document type matches that of Asset Separate by payment
     * 
     * @param assetGlobal
     * @return boolean
     */
    public boolean isAssetSeparateByPayment(AssetGlobal assetGlobal);
    
    /**
     * Add and return the total amount for all unique assets created.
     * 
     * @param assetGlobal
     * @return KualiDecimal
     */
    public KualiDecimal getUniqueAssetsTotalAmount(AssetGlobal assetGlobal);

    /**
     * Returns assets for save for create new assets
     * @param assetGlobal
     * @return
     */
    public List<PersistableBusinessObject> getCreateNewAssets(AssetGlobal assetGlobal);    
    
    /**
     * Returns assets for save for asset separate
     * @param assetGlobal
     * @return
     */
    public List<PersistableBusinessObject> getSeparateAssets(AssetGlobal assetGlobal);
    
    /**
     * @return the parameter value for the new acquisition type code
     */
    public String getNewAcquisitionTypeCode();
    /**
     * @return the parameter value for the capital object acquisition code group
     */
    public String getCapitalObjectAcquisitionCodeGroup();
    /**
     * @return the parameter value for the not new acquisition code group 
     */
    public String getNonNewAcquisitionCodeGroup();
    
    /**
     * Gets the FISCAL_YEAR_END_MONTH_AND_DAY system parameter
     * TODO This should be refactored to a generic parm accessible to any module.
     * @return FISCAL_YEAR_END_MONTH_AND_DAY value
     */
    public String getFiscalYearEndDayAndMonth();

}
