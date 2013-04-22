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
import org.kuali.kfs.coa.businessobject.SubAccountImportDetail;
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
public class SubAccountImportLineObjectExistenceValidation extends GenericValidation {
    private static final Logger LOG = Logger.getLogger(SubAccountImportLineObjectExistenceValidation.class);
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
        SubAccountImportDetail subAccountLine = (SubAccountImportDetail) importedLineForValidation;
        valid &= checkRefObjectExistence(subAccountLine);
        valid &= checkForPartiallyEnteredReportingFields(subAccountLine);
        return valid;
    }

    /**
     * The method checks the user entered object existing in system
     *
     * @param subAccountLine
     * @return
     */
    protected boolean checkRefObjectExistence(SubAccountImportDetail subAccountLine) {
        boolean valid = true;
        subAccountLine.refresh();

        if (StringUtils.isNotBlank(subAccountLine.getChartOfAccountsCode()) && ObjectUtils.isNull(subAccountLine.getChart())) {
            String label = dataDictionaryService.getDataDictionary().getBusinessObjectEntry(SubAccountImportDetail.class.getName()).getAttributeDefinition(KFSPropertyConstants.SubAccountImport.CHART_OF_ACCOUNTS_CODE).getLabel();
            GlobalVariables.getMessageMap().putError(KRADConstants.DOCUMENT_ERRORS, KFSKeyConstants.ERROR_MASSIMPORT_EXISTENCE, new String[] { subAccountLine.getSequenceNumber() == null ? "" : subAccountLine.getSequenceNumber().toString(), label });
        }

        if (StringUtils.isNotBlank(subAccountLine.getChartOfAccountsCode()) && StringUtils.isNotBlank(subAccountLine.getAccountNumber()) && ObjectUtils.isNull(subAccountLine.getAccount())) {
            String label = dataDictionaryService.getDataDictionary().getBusinessObjectEntry(SubAccountImportDetail.class.getName()).getAttributeDefinition(KFSPropertyConstants.SubAccountImport.ACCOUNT_NUMBER).getLabel();
            GlobalVariables.getMessageMap().putError(KRADConstants.DOCUMENT_ERRORS, KFSKeyConstants.ERROR_MASSIMPORT_EXISTENCE, new String[] { subAccountLine.getSequenceNumber() == null ? "" : subAccountLine.getSequenceNumber().toString(), label });
        }

        if (StringUtils.isNotBlank(subAccountLine.getFinancialReportChartCode()) && ObjectUtils.isNull(subAccountLine.getFinancialReportChart())) {
            String label = dataDictionaryService.getDataDictionary().getBusinessObjectEntry(SubAccountImportDetail.class.getName()).getAttributeDefinition(KFSPropertyConstants.SubAccountImport.FINANCIAL_REPORT_CHART_CODE).getLabel();
            GlobalVariables.getMessageMap().putError(KRADConstants.DOCUMENT_ERRORS, KFSKeyConstants.ERROR_MASSIMPORT_EXISTENCE, new String[] { subAccountLine.getSequenceNumber() == null ? "" : subAccountLine.getSequenceNumber().toString(), label });
        }

        if (StringUtils.isNotBlank(subAccountLine.getFinancialReportChartCode()) && StringUtils.isNotBlank(subAccountLine.getFinReportOrganizationCode()) && ObjectUtils.isNull(subAccountLine.getFinacialReportOrg())) {
            String label = dataDictionaryService.getDataDictionary().getBusinessObjectEntry(SubAccountImportDetail.class.getName()).getAttributeDefinition(KFSPropertyConstants.SubAccountImport.FIN_REPORT_ORGANIZATION_CODE).getLabel();
            GlobalVariables.getMessageMap().putError(KRADConstants.DOCUMENT_ERRORS, KFSKeyConstants.ERROR_MASSIMPORT_EXISTENCE, new String[] { subAccountLine.getSequenceNumber() == null ? "" : subAccountLine.getSequenceNumber().toString(), label });
        }

        if (StringUtils.isNotBlank(subAccountLine.getFinancialReportChartCode()) && StringUtils.isNotBlank(subAccountLine.getFinReportOrganizationCode()) && StringUtils.isNotBlank(subAccountLine.getFinancialReportingCode()) && ObjectUtils.isNull(subAccountLine.getReportingCode())) {
            String label = dataDictionaryService.getDataDictionary().getBusinessObjectEntry(SubAccountImportDetail.class.getName()).getAttributeDefinition(KFSPropertyConstants.SubAccountImport.FINANCIAL_REPORTING_CODE).getLabel();
            GlobalVariables.getMessageMap().putError(KRADConstants.DOCUMENT_ERRORS, KFSKeyConstants.ERROR_MASSIMPORT_EXISTENCE, new String[] { subAccountLine.getSequenceNumber() == null ? "" : subAccountLine.getSequenceNumber().toString(), label });
        }
        return valid;
    }

    /**
     * This checks that the reporting fields are entered altogether or none at all
     *
     * @return false if only one reporting field filled out and not all of them, true otherwise
     */
    protected boolean checkForPartiallyEnteredReportingFields(SubAccountImportDetail subAccountLine) {

        LOG.info("Entering checkExistenceAndActive()");

        boolean success = true;
        boolean allReportingFieldsEntered = false;
        boolean anyReportingFieldsEntered = false;

        // set a flag if all three reporting fields are filled (this is separated just for readability)
        if (StringUtils.isNotEmpty(subAccountLine.getFinancialReportChartCode()) && StringUtils.isNotEmpty(subAccountLine.getFinReportOrganizationCode()) && StringUtils.isNotEmpty(subAccountLine.getFinancialReportingCode())) {
            allReportingFieldsEntered = true;
        }

        // set a flag if any of the three reporting fields are filled (this is separated just for readability)
        if (StringUtils.isNotEmpty(subAccountLine.getFinancialReportChartCode()) || StringUtils.isNotEmpty(subAccountLine.getFinReportOrganizationCode()) || StringUtils.isNotEmpty(subAccountLine.getFinancialReportingCode())) {
            anyReportingFieldsEntered = true;
        }

        // if any of the three reporting code fields are filled out, all three must be, or none
        // if any of the three are entered
        if (anyReportingFieldsEntered && !allReportingFieldsEntered) {
            GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath(KRADConstants.DOCUMENT_ERRORS, KFSKeyConstants.ERROR_SUBACCOUNTIMPORT_PARTIALREPORTINGCODEFIELDSENTERED, new String[] { subAccountLine.getSequenceNumber() == null ? "" : subAccountLine.getSequenceNumber().toString() });
            success = false;
        }

        return success;
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
