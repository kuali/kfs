package edu.arizona.kfs.module.cr.service;

import edu.arizona.kfs.module.cr.businessobject.CheckReconciliation;

/**
 * Check Reconciliation Payee Service
 */
public interface CheckReconciliationPayeeService {

    /**
     * Retrieves the payee name associated with the {@link CheckReconciliation} record
     */
    public String getCheckPayeeName(CheckReconciliation checkReconciliation);
}
