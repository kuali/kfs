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
package org.kuali.kfs.module.cam.document;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.businessobject.BarcodeInventoryErrorDetail;
import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase;
public class BarcodeInventoryErrorDocument extends FinancialSystemTransactionalDocumentBase {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BarcodeInventoryErrorDocument.class);
    
	protected String documentNumber;
	protected String uploaderUniversalIdentifier;
	
    //global replace	- search fields
    //*** Old values **************
    protected String currentTagNumber;
    protected String currentScanCode;
    protected String currentCampusCode;
    protected String currentBuildingNumber;
    protected String currentRoom;
    protected String currentSubroom;
    protected String currentConditionCode;

    //*** New values **************
    protected String newTagNumber;
    protected String newScanCode;
    protected String newCampusCode;
    protected String newBuildingNumber;
    protected String newRoom;
    protected String newSubroom;
    protected String newConditionCode;
	
    protected List<BarcodeInventoryErrorDetail> barcodeInventoryErrorDetail;
    
	/**
	 * Default constructor.
	 */
	public BarcodeInventoryErrorDocument() {
	    super();
	    this.setBarcodeInventoryErrorDetail(new ArrayList<BarcodeInventoryErrorDetail>());	    
	}

	/**
	 * Gets the documentNumber attribute.
	 * 
	 * @return Returns the documentNumber
	 * 
	 */
	public String getDocumentNumber() { 
		return documentNumber;
	}

	/**
	 * Sets the documentNumber attribute.
	 * 
	 * @param documentNumber The documentNumber to set.
	 * 
	 */
	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}


	/**
	 * Gets the uploaderUniversalIdentifier attribute.
	 * 
	 * @return Returns the uploaderUniversalIdentifier
	 * 
	 */
	public String getUploaderUniversalIdentifier() { 
		return uploaderUniversalIdentifier;
	}

	/**
	 * Sets the uploaderUniversalIdentifier attribute.
	 * 
	 * @param uploaderUniversalIdentifier The uploaderUniversalIdentifier to set.
	 * 
	 */
	public void setUploaderUniversalIdentifier(String uploaderUniversalIdentifier) {
		this.uploaderUniversalIdentifier = uploaderUniversalIdentifier;
	}

    public List<BarcodeInventoryErrorDetail> getBarcodeInventoryErrorDetail() {
        return barcodeInventoryErrorDetail;
    }

    public void setBarcodeInventoryErrorDetail(List<BarcodeInventoryErrorDetail> barcodeInventoryErrorDetails) {
        this.barcodeInventoryErrorDetail = barcodeInventoryErrorDetails;
    }
    
    
    /**
     * Determines the document had all its records corrected
     * 
     * @return boolean
     */
    public boolean isDocumentCorrected() {
        for(BarcodeInventoryErrorDetail detail:this.getBarcodeInventoryErrorDetail() ) {
            if (detail.getErrorCorrectionStatusCode().equals(CamsConstants.BarCodeInventoryError.STATUS_CODE_ERROR))
                return false;
        }
        return true;
    }
    
    
    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();      
        m.put("documentNumber", this.documentNumber);
        return m;
    }

    public String getCurrentTagNumber() {
        return currentTagNumber;
    }

    public void setCurrentTagNumber(String currentTagNumber) {
        this.currentTagNumber = currentTagNumber;
    }

    public String getCurrentScanCode() {
        return currentScanCode;
    }

    public void setCurrentScanCode(String currentScanCode) {
        this.currentScanCode = currentScanCode;
    }

    public String getCurrentCampusCode() {
        return currentCampusCode;
    }

    public void setCurrentCampusCode(String currentCampusCode) {
        this.currentCampusCode = currentCampusCode;
    }

    public String getCurrentBuildingNumber() {
        return currentBuildingNumber;
    }

    public void setCurrentBuildingNumber(String currentBuildingNumber) {
        this.currentBuildingNumber = currentBuildingNumber;
    }

    public String getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(String currentRoom) {
        this.currentRoom = currentRoom;
    }

    public String getCurrentSubroom() {
        return currentSubroom;
    }

    public void setCurrentSubroom(String currentSubroom) {
        this.currentSubroom = currentSubroom;
    }

    public String getCurrentConditionCode() {
        return currentConditionCode;
    }

    public void setCurrentConditionCode(String currentConditionCode) {
        this.currentConditionCode = currentConditionCode;
    }

    public String getNewTagNumber() {
        return newTagNumber;
    }

    public void setNewTagNumber(String newTagNumber) {
        this.newTagNumber = newTagNumber;
    }

    public String getNewScanCode() {
        return newScanCode;
    }

    public void setNewScanCode(String newScanCode) {
        this.newScanCode = newScanCode;
    }

    public String getNewCampusCode() {
        return newCampusCode;
    }

    public void setNewCampusCode(String newCampusCode) {
        this.newCampusCode = newCampusCode;
    }

    public String getNewBuildingNumber() {
        return newBuildingNumber;
    }

    public void setNewBuildingNumber(String newBuildingNumber) {
        this.newBuildingNumber = newBuildingNumber;
    }

    public String getNewRoom() {
        return newRoom;
    }

    public void setNewRoom(String newRoom) {
        this.newRoom = newRoom;
    }

    public String getNewSubroom() {
        return newSubroom;
    }

    public void setNewSubroom(String newSubroom) {
        this.newSubroom = newSubroom;
    }

    public String getNewConditionCode() {
        return newConditionCode;
    }

    public void setNewConditionCode(String newConditionCode) {
        this.newConditionCode = newConditionCode;
    }
    
    public void resetSearchFields() {
        currentTagNumber="";
        currentScanCode="";
        currentCampusCode="";
        currentBuildingNumber="";
        currentRoom="";
        currentSubroom="";
        currentConditionCode="";
        newTagNumber="";
        newScanCode="";
        newCampusCode="";
        newBuildingNumber="";
        newRoom="";
        newSubroom="";
        newConditionCode="";        
    }
}
