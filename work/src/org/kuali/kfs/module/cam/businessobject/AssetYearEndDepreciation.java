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
