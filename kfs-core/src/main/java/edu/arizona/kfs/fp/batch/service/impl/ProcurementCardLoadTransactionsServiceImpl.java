package edu.arizona.kfs.fp.batch.service.impl;

import java.util.HashMap;

import edu.arizona.kfs.fp.businessobject.ProcurementCardTranAddItem;
import edu.arizona.kfs.fp.businessobject.ProcurementCardTranNonFuel;
import edu.arizona.kfs.fp.businessobject.ProcurementCardTransaction;
import edu.arizona.kfs.fp.businessobject.ProcurementCardTranShipSvc;
import edu.arizona.kfs.fp.businessobject.ProcurementCardTranTempSvc;
import edu.arizona.kfs.fp.businessobject.ProcurementCardTranTransportLeg;


public class ProcurementCardLoadTransactionsServiceImpl extends org.kuali.kfs.fp.batch.service.impl.ProcurementCardLoadTransactionsServiceImpl {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ProcurementCardLoadTransactionsServiceImpl.class);
   
    /**
     * Calls businessObjectService to remove all the procurement card transaction rows from the transaction load table.
     */
    @Override
    public void cleanTransactionsTable() {    	
    	super.businessObjectService.deleteMatching(ProcurementCardTranAddItem.class, new HashMap());
    	super.businessObjectService.deleteMatching(ProcurementCardTranNonFuel.class, new HashMap());        
        super.businessObjectService.deleteMatching(ProcurementCardTranShipSvc.class, new HashMap());
        super.businessObjectService.deleteMatching(ProcurementCardTranTempSvc.class, new HashMap());
        super.businessObjectService.deleteMatching(ProcurementCardTranTransportLeg.class, new HashMap());
        super.businessObjectService.deleteMatching(ProcurementCardTransaction.class, new HashMap());
    }
   
}
