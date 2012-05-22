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

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.GlobalBusinessObjectDetail;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;


public class AssetYearEndDepreciation extends PersistableBusinessObjectBase implements MutableInactivatable {
    private String documentNumber;
    private Integer universityFiscalYear;
    private Date runDate;
    private boolean active;
    private List<AssetYearEndDepreciationDetail> assetYearEndDepreciationDetails;
    private List<GeneralLedgerPendingEntry> generalLedgerPendingEntries;


    /**
     * Default constructor.
     */
    public AssetYearEndDepreciation() {
        assetYearEndDepreciationDetails = new ArrayList<AssetYearEndDepreciationDetail>();
        generalLedgerPendingEntries = new ArrayList<GeneralLedgerPendingEntry>();
        setActive(true);
    }


    /**
     * Get the document Number
     * 
     * @return documentNumber
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Set the document Number
     * 
     * @param documentNumber
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * @see org.kuali.rice.kns.bo.GlobalBusinessObject#generateGlobalChangesToPersist()
     */
    public List<PersistableBusinessObject> generateGlobalChangesToPersist() {
        List<PersistableBusinessObject> persistables = new ArrayList<PersistableBusinessObject>();
        for (Object detail : assetYearEndDepreciationDetails) {
            setAssetForPersist(((AssetYearEndDepreciationDetail) detail).getAsset(), persistables);
        }
        return persistables;
    }

    /**
     * Create an ArrayList
     * 
     * @return new ArrayList<PersistableBusinessObject>
     */
    public List<PersistableBusinessObject> generateDeactivationsToPersist() {
        List<PersistableBusinessObject> persistables = new ArrayList<PersistableBusinessObject>();
        return persistables;
    }

    /**
     * @see org.kuali.rice.kns.bo.PersistableBusinessObjectBase#buildListOfDeletionAwareLists()
     */
    @Override
    public List buildListOfDeletionAwareLists() {
        List<Collection<PersistableBusinessObject>> managedList = super.buildListOfDeletionAwareLists();
        managedList.add(new ArrayList <PersistableBusinessObject> (getAssetYearEndDepreciationDetails()));
        return managedList;
    }

    /**
     * This method set asset fields for update
     * 
     * @param persistables
     */
    private void setAssetForPersist(Asset asset, List<PersistableBusinessObject> persistables) {
        persistables.add(asset);
    }


    /**
     * return asset year end depreciation details
     * 
     * @return getAssetYearEndDepreciationDetails()
     */
    public List<? extends GlobalBusinessObjectDetail> getAllDetailObjects() {
        return getAssetYearEndDepreciationDetails();
    }

    /**
     * TRUE
     * 
     * @return true TODO why does this exist?
     */
    public boolean isPersistable() {
        return true;
    }


    /**
     * asset Year End Depreciation Details
     * 
     * @return assetYearEndDepreciationDetails
     */
    public List<AssetYearEndDepreciationDetail> getAssetYearEndDepreciationDetails() {
        return assetYearEndDepreciationDetails;
    }

    /**
     * Set assetYearEndDepreciationDetails
     * 
     * @param assetYearEndDepreciationDetails
     */
    public void setAssetYearEndDepreciationDetails(List<AssetYearEndDepreciationDetail> assetYearEndDepreciationDetails) {
        this.assetYearEndDepreciationDetails = assetYearEndDepreciationDetails;
    }

    /**
     * Get the list of general ledger pending entries
     * 
     * @return generalLedgerPendingEntries
     */
    public List<GeneralLedgerPendingEntry> getGeneralLedgerPendingEntries() {
        return generalLedgerPendingEntries;
    }

    /**
     * Set the list of generalLedgerPendingEntries
     * 
     * @param generalLedgerPendingEntries
     */
    public void setGeneralLedgerPendingEntries(List<GeneralLedgerPendingEntry> generalLedgerPendingEntries) {
        this.generalLedgerPendingEntries = generalLedgerPendingEntries;
    }

    /**
     * Get the University Fiscal year
     * 
     * @return universityFiscalYear
     */
    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    /**
     * Set the University Fiscal year
     * 
     * @param universityFiscalYear
     */
    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }

    /**
     * Get the RunDate
     * 
     * @return runDate
     */
    public Date getRunDate() {
        return runDate;
    }

    /**
     * Set the Run Date
     * 
     * @param runDate
     */
    public void setRunDate(Date runDate) {
        this.runDate = runDate;
    }

    /**
     * @see org.kuali.rice.kns.bo.Inactivateable#isActive()
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @see org.kuali.rice.core.api.mo.common.active.MutableInactivateable#setActive(boolean)
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap<String, Integer> toStringMapper() {
        LinkedHashMap<String, Integer> m = new LinkedHashMap<String, Integer>();
        m.put("universityFiscalYear", this.universityFiscalYear);
        return m;
    }
}