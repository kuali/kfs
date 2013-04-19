package org.kuali.kfs.coa.document.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.ProjectCode;
import org.kuali.kfs.coa.businessobject.ProjectCodeImportDetail;
import org.kuali.kfs.coa.document.service.ProjectCodeImportService;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * This class is the service implementation for the SubAccountImport document structure.
 */

public class ProjectCodeImportServiceImpl implements ProjectCodeImportService {
    private static final Logger LOG = Logger.getLogger(ProjectCodeImportServiceImpl.class);

    protected BusinessObjectService businessObjectService;

    @Override
    public void saveProjectCodeDetails(List<ProjectCodeImportDetail> projectCodeImportDetails) {
        List<ProjectCode> persistProjectCds = new ArrayList<ProjectCode>();
        ProjectCode projectCd1 = null;

        for (ProjectCodeImportDetail importLine : projectCodeImportDetails) {
            projectCd1 = new ProjectCode();

            projectCd1.setCode(importLine.getProjectCode());
            projectCd1.setChartOfAccountsCode(importLine.getChartOfAccountsCode());
            projectCd1.setName(importLine.getProjectName());
            projectCd1.setOrganizationCode(importLine.getOrganizationCode());
            projectCd1.setProjectDescription(importLine.getProjectDescription());
            projectCd1.setProjectManagerUniversalId(importLine.getProjectManagerUniversalId());
            projectCd1.setActive(importLine.isActive());

            persistProjectCds.add(projectCd1);
        }
        businessObjectService.save(persistProjectCds);
    }

    /**
     * Gets the businessObjectService attribute.
     *
     * @return Returns the businessObjectService.
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Sets the businessObjectService attribute value.
     *
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }


}
