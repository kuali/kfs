package org.kuali.kfs.coa.document.service;

import java.util.List;

import org.kuali.kfs.coa.businessobject.SubAccountImportDetail;
import org.kuali.kfs.coa.businessobject.SubObjectCodeImportDetail;

/**
 * This interface defines methods that a SubAccount Service must provide.
 */
public interface SubObjectCodeImportService {
    /**
     * Persistent subObjectCode with notification to SAP
     * 
     * @param subObjectCodeImportDetails
     */
    void saveSubObjectCodeDetails(List<SubObjectCodeImportDetail> subObjectCodeImportDetails);
}
