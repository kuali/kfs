package org.kuali.module.cams.document;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.kfs.document.FinancialSystemTransactionalDocumentBase;
import org.kuali.module.cams.bo.BarcodeInventoryErrorDetail;


public class BarcodeInventoryErrorDocument extends FinancialSystemTransactionalDocumentBase {
	private String documentNumber;
	private String uploaderUniversalIdentifier;
    
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

    public List<BarcodeInventoryErrorDetail> getBarcodeInventoryErrorDetail() {
        return barcodeInventoryErrorDetail;
    }

    public void setBarcodeInventoryErrorDetail(List<BarcodeInventoryErrorDetail> barcodeInventoryErrorDetails) {
        this.barcodeInventoryErrorDetail = barcodeInventoryErrorDetails;
    }

 
    public BarcodeInventoryErrorDetail getBarcodeInventoryErrorDetail(int index) {
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
