package edu.arizona.kfs.fp.document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.fp.businessobject.Check;
import org.kuali.kfs.fp.businessobject.CheckBase;
import org.kuali.kfs.fp.businessobject.CoinDetail;
import org.kuali.kfs.fp.businessobject.CurrencyDetail;
import org.kuali.kfs.fp.businessobject.DepositCashReceiptControl;
import org.kuali.kfs.fp.document.service.CashReceiptService;
import org.kuali.kfs.fp.document.validation.event.AddCheckEvent;
import org.kuali.kfs.fp.document.validation.event.DeleteCheckEvent;
import org.kuali.kfs.fp.document.validation.event.UpdateCheckEvent;
import org.kuali.kfs.fp.service.CheckService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSParameterKeyConstants;
import org.kuali.kfs.sys.KFSConstants.CurrencyCoinSources;
import org.kuali.kfs.sys.KFSConstants.DocumentStatusCodes;
import org.kuali.kfs.sys.KFSConstants.DocumentStatusCodes.CashReceipt;
import org.kuali.kfs.sys.businessobject.ChartOrgHolder;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.businessobject.SufficientFundsItem;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AmountTotaling;
import org.kuali.kfs.sys.document.service.DebitDeterminerService;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krms.impl.repository.ActionBo;
import org.kuali.rice.krad.util.ObjectUtils;

public class CashReceiptDocument extends org.kuali.kfs.fp.document.CashReceiptDocument {
	
    /**
     * Retrieve this cash receipt document's collection of checks from the database
     *
     * @return the collection of checks for this cash receipt document
     */
    protected List<Check> retrieveChecks() { 	
    	return (List<Check>) SpringContext.getBean(BusinessObjectService.class).findMatchingOrderBy(CheckBase.class, getCheckDetailskPrimaryKey(), "financialDocumentDepositLineNumber", true);
    }	  
    
    /**
     * Generate the primary key for a set of check details related to this document
     *
     * @return a map with a representation of the proper primary key
     */
    protected Map getCheckDetailskPrimaryKey() {
        Map pk = new HashMap();
        pk.put("documentNumber", this.getDocumentNumber());
        pk.put("financialDocumentTypeCode", CashReceiptDocument.DOCUMENT_TYPE);
        return pk;
    }    
             
    /**
     * @see org.kuali.rice.kns.document.DocumentBase#processAfterRetrieve()
     */
    @Override
    public void processAfterRetrieve() {
        super.processAfterRetrieve();
        
        // Retrieve checks from DB for current document
        setChecks(retrieveChecks());
        
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

