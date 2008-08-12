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
package org.kuali.kfs.module.cab.document.web.struts;

import org.kuali.core.web.struts.form.KualiForm;
import org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntry;
import org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntryAsset;
import org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntryAssetDetail;
import org.kuali.kfs.module.cam.businessobject.Asset;

public class GlLineForm extends KualiForm {

    private GeneralLedgerEntry generalLedgerEntry;
    private String capitalAssetNumber;
    private boolean newAssetIndicator;
    private Asset asset;
    private GeneralLedgerEntryAsset newGeneralLedgerEntryAsset;
    private GeneralLedgerEntryAssetDetail newGeneralLedgerEntryAssetDetail;

    /**
     * Gets the generalLedgerEntry attribute.
     * 
     * @return Returns the generalLedgerEntry.
     */
    public GeneralLedgerEntry getGeneralLedgerEntry() {
        return generalLedgerEntry;
    }

    /**
     * Sets the generalLedgerEntry attribute value.
     * 
     * @param generalLedgerEntry The generalLedgerEntry to set.
     */
    public void setGeneralLedgerEntry(GeneralLedgerEntry generalLedgerEntry) {
        this.generalLedgerEntry = generalLedgerEntry;
    }

    /**
     * Gets the capitalAssetNumber attribute.
     * 
     * @return Returns the capitalAssetNumber.
     */
    public String getCapitalAssetNumber() {
        return capitalAssetNumber;
    }

    /**
     * Sets the capitalAssetNumber attribute value.
     * 
     * @param capitalAssetNumber The capitalAssetNumber to set.
     */
    public void setCapitalAssetNumber(String capitalAssetNumber) {
        this.capitalAssetNumber = capitalAssetNumber;
    }


    /**
     * Gets the newAssetIndicator attribute.
     * 
     * @return Returns the newAssetIndicator.
     */
    public boolean isNewAssetIndicator() {
        return newAssetIndicator;
    }

    /**
     * Sets the newAssetIndicator attribute value.
     * 
     * @param newAssetIndicator The newAssetIndicator to set.
     */
    public void setNewAssetIndicator(boolean newAssetIndicator) {
        this.newAssetIndicator = newAssetIndicator;
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
     */
    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    /**
     * Gets the newGeneralLedgerEntryAsset attribute.
     * 
     * @return Returns the newGeneralLedgerEntryAsset.
     */
    public GeneralLedgerEntryAsset getNewGeneralLedgerEntryAsset() {
        return newGeneralLedgerEntryAsset;
    }

    /**
     * Sets the newGeneralLedgerEntryAsset attribute value.
     * 
     * @param newGeneralLedgerEntryAsset The newGeneralLedgerEntryAsset to set.
     */
    public void setNewGeneralLedgerEntryAsset(GeneralLedgerEntryAsset newGeneralLedgerEntryAsset) {
        this.newGeneralLedgerEntryAsset = newGeneralLedgerEntryAsset;
    }

    /**
     * Gets the newGeneralLedgerEntryAssetDetail attribute.
     * 
     * @return Returns the newGeneralLedgerEntryAssetDetail.
     */
    public GeneralLedgerEntryAssetDetail getNewGeneralLedgerEntryAssetDetail() {
        return newGeneralLedgerEntryAssetDetail;
    }

    /**
     * Sets the newGeneralLedgerEntryAssetDetail attribute value.
     * 
     * @param newGeneralLedgerEntryAssetDetail The newGeneralLedgerEntryAssetDetail to set.
     */
    public void setNewGeneralLedgerEntryAssetDetail(GeneralLedgerEntryAssetDetail newGeneralLedgerEntryAssetDetail) {
        this.newGeneralLedgerEntryAssetDetail = newGeneralLedgerEntryAssetDetail;
    }
}
