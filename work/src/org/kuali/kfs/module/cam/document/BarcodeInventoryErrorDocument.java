package org.kuali.kfs.module.cam.document;

import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.core.rule.event.KualiDocumentEvent;
import org.kuali.core.util.TypedArrayList;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.businessobject.BarcodeInventoryErrorDetail;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase;


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
     * 
     * This method returns a particular element of the BCIE collection
     * @param index
     * @return
     *
    public BarcodeInventoryErrorDetail getBarcodeInventoryErrorDetail(int index) {
        if (index >= barcodeInventoryErrorDetail.size()) {
            for (int i = barcodeInventoryErrorDetail.size(); i <= index; i++) {
                barcodeInventoryErrorDetail.add(new BarcodeInventoryErrorDetail());
            }
        }
        return barcodeInventoryErrorDetail.get(index);
    } */ 
    
    /**
     * 
     * This method removes elements from the collection that holds the BCIE record detail
     * @param index
     */
//    public void deleteBarcodeInventoryErrorDetail(int index) {
//        barcodeInventoryErrorDetail.remove(index);
//    }   
    
    /**
     * 
     * @see org.kuali.core.document.DocumentBase#validateBusinessRules(org.kuali.core.rule.event.KualiDocumentEvent)
     * 
     * Left empty in order to prevent rule validation when saving the document.
     */
//    @Override    
//    public void validateBusinessRules(KualiDocumentEvent event) {
//    }
    

    /*
    public void setBarcodeInventoryErrorSelectedRows(boolean flag) {
        for (int i=0;i<this.getBarcodeInventoryErrorDetail().size();i++){
            this.getBarcodeInventoryErrorDetail().get(i).setRowSelected(flag);
        }        
    }*/
    
        
//    public void processAfterRetrieve() {
//        super.processAfterRetrieve();
//        LOG.info("****processAfterRetrieve()");
//        for(int i=0;i<this.getBarcodeInventoryErrorDetail().size();i++) {
//            if (this.getBarcodeInventoryErrorDetail(i).getErrorCorrectionStatusCode().equals(CamsConstants.BarcodeInventoryError.STATUS_CODE_ERROR)) {
//                getBarcodeInventoryErrorDetail().remove(i);
//                i=0;                
//            }
//        }        
//    }

    
//    @Override
//    public List buildListOfDeletionAwareLists() {
//        List managedLists = super.buildListOfDeletionAwareLists();
//        managedLists.add(this.getBarcodeInventoryErrorDetail());
//        return managedLists;
//    }
    
    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();      
        m.put("documentNumber", this.documentNumber);
        return m;
    }
}
