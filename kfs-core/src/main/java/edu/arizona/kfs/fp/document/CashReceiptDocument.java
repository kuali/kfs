package edu.arizona.kfs.fp.document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.fp.businessobject.Check;
import org.kuali.kfs.fp.businessobject.CheckBase;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;

public class CashReceiptDocument extends org.kuali.kfs.fp.document.CashReceiptDocument {
	
    /**
     * Retrieve this cash receipt document's collection of checks from the database
     *
     * @param primarykeyMap Map of the primary keys for check details
     * @param sortProperty the property to sort check details by
     * @param sortAscending boolean indicating whether to sort ascending
     * @return the collection of checks for this cash receipt document
     */
    protected List<Check> retrieveChecks(Map primarykeyMap, String sortProperty, boolean sortAscending) { 	
    	return (List<Check>) SpringContext.getBean(BusinessObjectService.class).findMatchingOrderBy(CheckBase.class, primarykeyMap, sortProperty, sortAscending);    	
    }			
               
    /**
     * Generate the primary key for a set of check details related to this document
     *
     * @param documentNumberKey the key for documentNumber
     * @param documentNumberValue the value for documentNumber
     * @param docTypeCodeKey the key for document type code
     * @param docTypeCodeValue the value for document type code
     * @return a map with a representation of the proper primary key
     */
    protected Map<String, String> getCheckDetailskPrimaryKey(String documentNumberKey, String documentNumberValue, String docTypeCodeKey, String docTypeCodeValue) {
        Map<String, String> pk = new HashMap<String, String>();
        pk.put(documentNumberKey, documentNumberValue);
        pk.put(docTypeCodeKey, docTypeCodeValue);    
        return pk;
    }       
       
    /**
     * @see org.kuali.rice.kns.document.DocumentBase#processAfterRetrieve()
     */
    @Override
    public void processAfterRetrieve() {
        super.processAfterRetrieve();
        
        // Retrieve checks from DB for current document
        setChecks(retrieveChecks(getCheckDetailskPrimaryKey("documentNumber", this.getDocumentNumber(), KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE, CashReceiptDocument.DOCUMENT_TYPE), KFSPropertyConstants.FINANCIAL_DOCUMENT_DEPOSIT_LINE_NUMBER, true));        
        
        // set to checkTotal mode if no checks
        List<Check> checkList = getChecks();
        if (ObjectUtils.isNull(checkList) || checkList.isEmpty()) {
            setCheckEntryMode(CHECK_ENTRY_TOTAL);
        }
        // set to checkDetail mode if checks exist (and update the checkTotal, while you're here)
        else {
            setCheckEntryMode(CHECK_ENTRY_DETAIL);
            setTotalCheckAmount(calculateCheckTotal());
        }
        refreshCashDetails();
    }    
               
}

