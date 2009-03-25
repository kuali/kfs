package org.kuali.kfs.module.cam.document;

import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.kfs.module.cam.businessobject.BarcodeInventoryErrorDetail;
import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase;
import org.kuali.rice.kns.util.TypedArrayList;
public class BarcodeInventoryErrorDocument extends FinancialSystemTransactionalDocumentBase {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BarcodeInventoryErrorDocument.class);
    
	private String documentNumber;
	private String uploaderUniversalIdentifier;
	
    //global replace	- search fields
    //*** Old values **************
    private String currentTagNumber;
    private String currentScanCode;
    private String currentCampusCode;
    private String currentBuildingNumber;
    private String currentRoom;
    private String currentSubroom;
    private String currentConditionCode;

    //*** New values **************
    private String newTagNumber;
    private String newScanCode;
    private String newCampusCode;
    private String newBuildingNumber;
    private String newRoom;
    private String newSubroom;
    private String newConditionCode;
	
    private List<BarcodeInventoryErrorDetail> barcodeInventoryErrorDetail;
    
	/**
	 * Default constructor.
	 */
	public BarcodeInventoryErrorDocument() {
	    super();
	    this.setBarcodeInventoryErrorDetail(new TypedArrayList(BarcodeInventoryErrorDetail.class));	    
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
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
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
