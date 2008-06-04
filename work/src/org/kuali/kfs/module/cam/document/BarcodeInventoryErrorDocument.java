package org.kuali.module.cams.document;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.document.TransactionalDocumentBase;
import org.kuali.module.ar.bo.CashControlDetail;
import org.kuali.module.cams.bo.BarcodeInventoryErrorDetail;


public class BarcodeInventoryErrorDocument extends TransactionalDocumentBase {
	private String documentNumber;
	private String uploaderUniversalIdentifier;
    private DocumentHeader documentHeader;
    private List<BarcodeInventoryErrorDetail> barcodeInventoryErrorDetail;
    
	/**
	 * Default constructor.
	 */
	public BarcodeInventoryErrorDocument() {
	    barcodeInventoryErrorDetail = new ArrayList<BarcodeInventoryErrorDetail>();        
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

	/**
	 * Gets the documentHeader attribute.
	 * 
	 * @return Returns the documentHeader
	 * 
	 */
	public DocumentHeader getDocumentHeader() { 
		return documentHeader;
	}

	/**
	 * Sets the documentHeader attribute.
	 * 
	 * @param documentHeader The documentHeader to set.
	 * @deprecated
	 */
	public void setDocumentHeader(DocumentHeader documentHeader) {
		this.documentHeader = documentHeader;
	}

    /**
     * Gets the inventoryUploadErrorDetails attribute. 
     * @return Returns the inventoryUploadErrorDetails.
     */
    public List<BarcodeInventoryErrorDetail> getInventoryUploadErrorDetail() {
        return barcodeInventoryErrorDetail;
    }

    /**
     * Sets the inventoryUploadErrorDetails attribute value.
     * @param inventoryUploadErrorDetails The inventoryUploadErrorDetails to set.
     */
    public void setInventoryUploadErrorDetail(List<BarcodeInventoryErrorDetail> inventoryUploadErrorDetails) {
        this.barcodeInventoryErrorDetail = inventoryUploadErrorDetails;
    }

 
    public BarcodeInventoryErrorDetail getInventoryUploadErrorDetail(int index) {
        if (index >= barcodeInventoryErrorDetail.size()) {
            for (int i = barcodeInventoryErrorDetail.size(); i <= index; i++) {
                barcodeInventoryErrorDetail.add(new BarcodeInventoryErrorDetail());
            }
        }
        return barcodeInventoryErrorDetail.get(index);
    }  
    

    public void deleteCashControlDetail(int index) {
        barcodeInventoryErrorDetail.remove(index);
    }   
    
    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();      
        m.put("documentNumber", this.documentNumber);
        return m;
    }

}
