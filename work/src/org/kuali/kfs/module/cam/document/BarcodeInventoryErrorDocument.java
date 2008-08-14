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
}
