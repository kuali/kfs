package org.kuali.kfs.module.cam.document;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase;
import org.kuali.kfs.module.cam.businessobject.BarcodeInventoryErrorDetail;


public class BarcodeInventoryErrorDocument extends FinancialSystemTransactionalDocumentBase {
	private String documentNumber;
	private String uploaderUniversalIdentifier;
    
    private List<BarcodeInventoryErrorDetail> barcodeInventoryErrorDetail;
    private FinancialSystemDocumentHeader documentHeader = new FinancialSystemDocumentHeader();    
    
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
	public FinancialSystemDocumentHeader getDocumentHeader() { 
		return documentHeader;
	}

	/**
	 * Sets the documentHeader attribute.
	 * 
	 * @param documentHeader The documentHeader to set.
	 * @deprecated
	 */
	public void setDocumentHeader(FinancialSystemDocumentHeader documentHeader) {
		this.documentHeader = documentHeader;
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
    

    public void deleteBarcodeInventoryErrorDetail(int index) {
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
