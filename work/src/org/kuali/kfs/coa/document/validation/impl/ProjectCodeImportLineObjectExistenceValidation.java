/*
 * Copyright 2008 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.coa.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.ProjectCodeImportDetail;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.MassImportLineBase;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * A validation to have the data dictionary perform its validations upon a business object
 */
public class ProjectCodeImportLineObjectExistenceValidation extends GenericValidation {
    private static final Logger LOG = Logger.getLogger(ProjectCodeImportLineObjectExistenceValidation.class);
    private DataDictionaryService dataDictionaryService;
    private MassImportLineBase importedLineForValidation;

    /**
     * Validates a business object against the data dictionary <strong>expects a business object to be the first parameter</strong>
     *
     * @see org.kuali.kfs.sys.document.validation.GenericValidation#validate(java.lang.Object[])
     */
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;
        ProjectCodeImportDetail projectCdImpLine = (ProjectCodeImportDetail) importedLineForValidation;

        valid &= checkRefObjectExistence(projectCdImpLine);
        valid &= checkProjectCodeValid(projectCdImpLine);
        return valid;
    }

    /**
     * Check project code valid
     *
     * @param projectCdImpLine
     * @return
     */
    protected boolean checkProjectCodeValid(ProjectCodeImportDetail projectCdImpLine) {
        boolean valid = true;
        // check project code can't have leading zero
        if (StringUtils.isNotBlank(projectCdImpLine.getProjectCode()) && projectCdImpLine.getProjectCode().startsWith("0")) {
            valid = false;
            String label = dataDictionaryService.getDataDictionary().getBusinessObjectEntry(ProjectCodeImportDetail.class.getName()).getAttributeDefinition(KFSPropertyConstants.ProjectCodeImport.PROJECT_CODE).getLabel();
            GlobalVariables.getMessageMap().putError(KRADConstants.DOCUMENT_ERRORS, KFSKeyConstants.ERROR_PROJECTCODEIMPORT_LEADINGZERONOTALLOWED, new String[] { projectCdImpLine.getSequenceNumber() == null ? "" : projectCdImpLine.getSequenceNumber().toString(), label });
        }
        return valid;
    }

    /**
     * The method checks the user entered object existing in system
     *
     * @param subAccountLine
     * @return
     */
    protected boolean checkRefObjectExistence(ProjectCodeImportDetail projectImpLine) {
        boolean valid = true;
        projectImpLine.refresh();

        if (StringUtils.isNotBlank(projectImpLine.getChartOfAccountsCode()) && ObjectUtils.isNull(projectImpLine.getChartOfAccounts())) {
            String label = dataDictionaryService.getDataDictionary().getBusinessObjectEntry(ProjectCodeImportDetail.class.getName()).getAttributeDefinition(KFSPropertyConstants.ProjectCodeImport.CHART_OF_ACCOUNTS_CODE).getLabel();
            GlobalVariables.getMessageMap().putError(KRADConstants.DOCUMENT_ERRORS, KFSKeyConstants.ERROR_MASSIMPORT_EXISTENCE, new String[] { projectImpLine.getSequenceNumber() == null ? "" : projectImpLine.getSequenceNumber().toString(), label });
        }


        if (StringUtils.isNotBlank(projectImpLine.getChartOfAccountsCode()) && StringUtils.isNotBlank(projectImpLine.getOrganizationCode()) && ObjectUtils.isNull(projectImpLine.getOrganization())) {
            String label = dataDictionaryService.getDataDictionary().getBusinessObjectEntry(ProjectCodeImportDetail.class.getName()).getAttributeDefinition(KFSPropertyConstants.ProjectCodeImport.ORGANIZATION_CODE).getLabel();
            GlobalVariables.getMessageMap().putError(KRADConstants.DOCUMENT_ERRORS, KFSKeyConstants.ERROR_MASSIMPORT_EXISTENCE, new String[] { projectImpLine.getSequenceNumber() == null ? "" : projectImpLine.getSequenceNumber().toString(), label });
        }

        return valid;
    }


    /**
     * Gets the dataDictionaryService attribute.
     *
     * @return Returns the dataDictionaryService.
     */
    public DataDictionaryService getDataDictionaryService() {
        return dataDictionaryService;
    }

    /**
     * Sets the dataDictionaryService attribute value.
     *
     * @param dataDictionaryService The dataDictionaryService to set.
     */
    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    /**
     * Gets the importedLineForValidation attribute.
     *
     * @return Returns the importedLineForValidation.
     */
    public MassImportLineBase getImportedLineForValidation() {
        return importedLineForValidation;
    }

    /**
     * Sets the importedLineForValidation attribute value.
     *
     * @param importedLineForValidation The importedLineForValidation to set.
     */
    public void setImportedLineForValidation(MassImportLineBase importedLineForValidation) {
        this.importedLineForValidation = importedLineForValidation;
    }


}
