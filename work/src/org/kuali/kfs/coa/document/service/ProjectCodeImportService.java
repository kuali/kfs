package org.kuali.kfs.coa.document.service;

import java.util.List;

import org.kuali.kfs.coa.businessobject.ProjectCodeImportDetail;

/**
 * This interface defines methods that a SubAccount Service must provide.
 */
public interface ProjectCodeImportService {
    /**
     * Persistent ProjectCode with notification to SAP
     * 
     * @param projectCodeImportDetails
     */
    void saveProjectCodeDetails(List<ProjectCodeImportDetail> projectCodeImportDetails);
}
