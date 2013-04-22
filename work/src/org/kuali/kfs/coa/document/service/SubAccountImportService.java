package org.kuali.kfs.coa.document.service;

import java.util.List;

import org.kuali.kfs.coa.businessobject.SubAccountImportDetail;

/**
 * This interface defines methods that a SubAccount Service must provide.
 */
public interface SubAccountImportService {
    /**
     * Persistent subAccount and a21SubAccount with notification to SAP
     * 
     * @param subAccountImportDetails
     */
    void saveSubAccounts(List<SubAccountImportDetail> subAccountImportDetails);
}
