package org.kuali.kfs.module.purap.batch.service;

import org.kuali.kfs.module.purap.businessobject.AutoClosePurchaseOrderView;

public interface AutoClosePurchaseOrderService {
    /**
     * This gets a list of Purchase Orders in Open status and checks to see if their
     * line item encumbrances are all fully disencumbered and if so then the Purchase
     * Order is closed and notes added to indicate the change occurred in batch
     *
     * @return boolean true if the job is completed successfully and false otherwise.
     */
    public boolean autoCloseFullyDisencumberedOrders();

    public void autoClosePurchaseOrder(AutoClosePurchaseOrderView poAutoClose);
}
