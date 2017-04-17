package edu.arizona.kfs.module.purap.document.service;

import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.vnd.businessobject.PurchaseOrderCostSource;

public interface PurapService extends org.kuali.kfs.module.purap.document.service.PurapService {
    
    /**
     * determine whether the item type is conflicted with tax policy
     * 
     * @param purchasingAccountsPayableDocument the given purap document
     * @param item the given item
     * @return true if the item type is conflicted with tax policy
     */
    public boolean isItemTypeConflictWithTaxPolicy(PurchasingAccountsPayableDocument purchasingAccountsPayableDocument, PurApItem item);
}
