/*
 * Copyright 2009 The Kuali Foundation.
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.coa.document;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.ProjectCodeImportDetail;
import org.kuali.kfs.coa.document.service.ProjectCodeImportService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants.ProjectCodeImport;
import org.kuali.kfs.sys.businessobject.MassImportLineBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase;
import org.kuali.kfs.sys.document.MassImportDocument;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;
import org.kuali.rice.kim.api.identity.IdentityService;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;

/**
 * This class is the global business object for Sub Account Import Global Maintenance Document
 */
public class ProjectCodeImportDocument extends FinancialSystemTransactionalDocumentBase implements MassImportDocument {
    private static final Logger Log = Logger.getLogger(ProjectCodeImportDocument.class);

    protected Integer nextItemLineNumber;

    private static IdentityService identityManagementService;

    private List<ProjectCodeImportDetail> projectCodeImportDetails;

    public ProjectCodeImportDocument() {
        super();
        this.nextItemLineNumber = new Integer(1);
        setProjectCodeImportDetails(new ArrayList<ProjectCodeImportDetail>());
    }


    @Override
    public void doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) {
        super.doRouteStatusChange(statusChangeEvent);

        WorkflowDocument workflowDocument = getDocumentHeader().getWorkflowDocument();

        // Workflow Status of PROCESSED --> Kuali Doc Status of Verified
        if (workflowDocument.isProcessed()) {
            SpringContext.getBean(ProjectCodeImportService.class).saveProjectCodeDetails(projectCodeImportDetails);
        }
    }


    /**
     * @see org.kuali.rice.kns.bo.PersistableBusinessObjectBase#buildListOfDeletionAwareLists()
     */
    @Override
    public List buildListOfDeletionAwareLists() {
        List managedLists = super.buildListOfDeletionAwareLists();
        managedLists.add(getProjectCodeImportDetails());
        return managedLists;
    }

    /**
     * Gets the projectCodeImportDetails attribute.
     *
     * @return Returns the projectCodeImportDetails.
     */
    public List<ProjectCodeImportDetail> getProjectCodeImportDetails() {
        return projectCodeImportDetails;
    }

    /**
     * Sets the projectCodeImportDetails attribute value.
     *
     * @param projectCodeImportDetails The projectCodeImportDetails to set.
     */
    public void setProjectCodeImportDetails(List<ProjectCodeImportDetail> projectCodeImportDetails) {
        this.projectCodeImportDetails = projectCodeImportDetails;
    }

    /**
     * Retrieve a particular line at a given index in the list of details.
     *
     * @param index
     * @return Check
     */
    public ProjectCodeImportDetail getProjectCodeImportDetail(int index) {
        while (this.projectCodeImportDetails.size() <= index) {
            projectCodeImportDetails.add(new ProjectCodeImportDetail());
        }
        return projectCodeImportDetails.get(index);
    }


    @Override
    public Class getImportLineClass() {
        return ProjectCodeImportDetail.class;
    }


    @Override
    public List getImportDetailCollection() {
        return projectCodeImportDetails;
    }

    /**
     * @see org.kuali.kfs.coa.document.MassImportDocument#customizeImportedLines(java.util.List)
     */
    @Override
    public void customizeImportedLines(List<MassImportLineBase> importedLines) {
        for (Iterator iterator = importedLines.iterator(); iterator.hasNext();) {
            ProjectCodeImportDetail importedLine = (ProjectCodeImportDetail) iterator.next();
            importedLine.setActive(true);
            if (StringUtils.isNotBlank(importedLine.getProjectManagerPrincipalName())) {
                // set to lower case of principalName since principal table always save as lower case
                importedLine.setProjectManagerPrincipalName(importedLine.getProjectManagerPrincipalName().toLowerCase());

                // set principal
                Principal principal = getIdentityManagementService().getPrincipalByPrincipalName(importedLine.getProjectManagerPrincipalName());
                if (principal != null && StringUtils.isNotBlank(principal.getPrincipalId())) {
                    importedLine.setProjectManagerUniversalId(principal.getPrincipalId());
                }
            }
        }
    }

    /**
     * @see org.kuali.kfs.coa.document.MassImportDocument#getErrorPathPrefix()
     */
    @Override
    public String getErrorPathPrefix() {
        return KFSConstants.ProjectCodeImportConstants.IMPORT_LINE_ERROR_PREFIX;
    }

    /**
     * Gets the nextItemLineNumber attribute.
     *
     * @return Returns the nextItemLineNumber.
     */
    @Override
    public Integer getNextItemLineNumber() {
        return nextItemLineNumber;
    }

    /**
     * Sets the nextItemLineNumber attribute value.
     *
     * @param nextItemLineNumber The nextItemLineNumber to set.
     */
    @Override
    public void setNextItemLineNumber(Integer nextItemLineNumber) {
        this.nextItemLineNumber = nextItemLineNumber;
    }

    @Override
    public String getFullErrorPathPrefix() {
        return KFSConstants.DOCUMENT_PROPERTY_NAME + "." + KFSPropertyConstants.ProjectCodeImport.PROJECT_CODE_IMPORT_DETAILS;
    }

    public IdentityService getIdentityManagementService() {
        if (identityManagementService == null) {
            identityManagementService = KimApiServiceLocator.getIdentityService();
        }
        return identityManagementService;
    }


    @Override
    public String[] getOrderedFieldList() {
        return new String[] { ProjectCodeImport.PROJECT_CODE, ProjectCodeImport.PROJECT_NAME, ProjectCodeImport.PROJECT_MANAGER_PRINCIPAL_NAME, ProjectCodeImport.CHART_OF_ACCOUNTS_CODE, ProjectCodeImport.ORGANIZATION_CODE, ProjectCodeImport.PROJECT_DESCRIPTION };
    }


}
