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
import org.kuali.kfs.coa.businessobject.SubObjectCodeImportDetail;
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
 * Generic validation to check existence of referenced account and object code
 */
public class SubObjectCodeImportLineObjectExistenceValidation extends GenericValidation {
    private DataDictionaryService dataDictionaryService;
    private MassImportLineBase importedLineForValidation;

    /**
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;
        valid &= checkRefObjectExistence();
        valid &= checkAccountActive();
        return valid;
    }

    /**
     * Check if account is active
     *
     * @return true if account is active
     */
    protected boolean checkAccountActive() {
        SubObjectCodeImportDetail subObjectCdLine = (SubObjectCodeImportDetail) importedLineForValidation;
        boolean valid = true;
        // disallow closed accounts
        if (ObjectUtils.isNotNull(subObjectCdLine.getAccount()) && !subObjectCdLine.getAccount().isActive()) {
            // if the account is closed
            String label = dataDictionaryService.getDataDictionary().getBusinessObjectEntry(SubObjectCodeImportDetail.class.getName()).getAttributeDefinition(KFSPropertyConstants.SubAccountImport.ACCOUNT_NUMBER).getLabel();
            GlobalVariables.getMessageMap().putError(KRADConstants.DOCUMENT_ERRORS, KFSKeyConstants.ERROR_SUBOBJECTCODEIMPORT_ACCOUNTMAYNOTBECLOSED, new String[] { subObjectCdLine.getSequenceNumber() == null ? "" : subObjectCdLine.getSequenceNumber().toString(), label });
            valid = false;
        }

        return valid;
    }

    /**
     * The method checks the user entered object existing in system
     *
     * @param subAccountLine
     * @return true if reference exists
     */
    protected boolean checkRefObjectExistence() {
        SubObjectCodeImportDetail subObjectImpLine = (SubObjectCodeImportDetail) importedLineForValidation;
        boolean valid = true;
        subObjectImpLine.refresh();

        if (StringUtils.isNotBlank(subObjectImpLine.getUniversityFiscalYear().toString()) && ObjectUtils.isNull(subObjectImpLine.getUniversityFiscal())) {
            String label = dataDictionaryService.getDataDictionary().getBusinessObjectEntry(SubObjectCodeImportDetail.class.getName()).getAttributeDefinition(KFSPropertyConstants.SubObjectCodeImport.UNIVERSITY_FISCAL_YEAR).getLabel();
            GlobalVariables.getMessageMap().putError(KRADConstants.DOCUMENT_ERRORS, KFSKeyConstants.ERROR_MASSIMPORT_EXISTENCE, new String[] { subObjectImpLine.getSequenceNumber() == null ? "" : subObjectImpLine.getSequenceNumber().toString(), label });
            valid = false;
        }


        if (StringUtils.isNotBlank(subObjectImpLine.getChartOfAccountsCode()) && ObjectUtils.isNull(subObjectImpLine.getChartOfAccounts())) {
            String label = dataDictionaryService.getDataDictionary().getBusinessObjectEntry(SubObjectCodeImportDetail.class.getName()).getAttributeDefinition(KFSPropertyConstants.SubObjectCodeImport.CHART_OF_ACCOUNTS_CODE).getLabel();
            GlobalVariables.getMessageMap().putError(KRADConstants.DOCUMENT_ERRORS, KFSKeyConstants.ERROR_MASSIMPORT_EXISTENCE, new String[] { subObjectImpLine.getSequenceNumber() == null ? "" : subObjectImpLine.getSequenceNumber().toString(), label });
            valid = false;
        }

        if (StringUtils.isNotBlank(subObjectImpLine.getChartOfAccountsCode()) && StringUtils.isNotBlank(subObjectImpLine.getAccountNumber()) && ObjectUtils.isNull(subObjectImpLine.getAccount())) {
            String label = dataDictionaryService.getDataDictionary().getBusinessObjectEntry(SubObjectCodeImportDetail.class.getName()).getAttributeDefinition(KFSPropertyConstants.SubAccountImport.ACCOUNT_NUMBER).getLabel();
            GlobalVariables.getMessageMap().putError(KRADConstants.DOCUMENT_ERRORS, KFSKeyConstants.ERROR_MASSIMPORT_EXISTENCE, new String[] { subObjectImpLine.getSequenceNumber() == null ? "" : subObjectImpLine.getSequenceNumber().toString(), label });
            valid = false;
        }

        if (StringUtils.isNotBlank(subObjectImpLine.getUniversityFiscalYear().toString()) && StringUtils.isNotBlank(subObjectImpLine.getChartOfAccountsCode()) && StringUtils.isNotBlank(subObjectImpLine.getFinancialObjectCode()) && ObjectUtils.isNull(subObjectImpLine.getFinancialObject())) {
            String label = dataDictionaryService.getDataDictionary().getBusinessObjectEntry(SubObjectCodeImportDetail.class.getName()).getAttributeDefinition(KFSPropertyConstants.SubObjectCodeImport.FIN_OBJECT_CODE).getLabel();
            GlobalVariables.getMessageMap().putError(KRADConstants.DOCUMENT_ERRORS, KFSKeyConstants.ERROR_MASSIMPORT_EXISTENCE, new String[] { subObjectImpLine.getSequenceNumber() == null ? "" : subObjectImpLine.getSequenceNumber().toString(), label });
            valid = false;
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
