/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.cam.document.service;

import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.module.cam.businessobject.AssetGlobal;
import org.kuali.kfs.module.cam.document.gl.CamsGeneralLedgerPendingEntrySourceBase;
import org.kuali.rice.kns.util.KualiDecimal;


/**
 * The interface defines methods for Asset Document
 */
public interface AssetGlobalService {
    /**
     * 
     * To calculate the total payment amounts for each asset.
     * 
     * @param assetGlobal
     * @return
     */
    public KualiDecimal totalPaymentByAsset(AssetGlobal assetGlobal);

    /**
     * 
     * This method checks if member exists in the given group.
     * 
     * @param groupName
     * @param memberName
     * @return
     */
    public boolean existsInGroup(String groupName, String memberName);

    /**
     * 
     * To calculate the total non federal contribution payment amounts for each asset.
     * 
     * @param assetGlobal
     * @return
     */
    public KualiDecimal totalNonFederalPaymentByAsset(AssetGlobal assetGlobal);

    /**
     * Creates GL Postables
     */
    public void createGLPostables(AssetGlobal assetGlobal, CamsGeneralLedgerPendingEntrySourceBase assetGlobalGlPoster);


    /**
     * 
     * To check if the objectCode can derive a capital asset by its object sub type code.
     * 
     * @param objectCode
     * @return
     */
    public boolean isCapitablObjectCode(ObjectCode objectCode);
    
    /**
     * Validates if the document type matches that of Asset Separate.
     * 
     * @param assetGlobal
     * @return boolean
     */
    public boolean isAssetSeparateDocument(AssetGlobal assetGlobal);
}
