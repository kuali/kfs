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

import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.businessobject.SubAccountImportDetail;
import org.kuali.kfs.coa.document.SubAccountImportDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.MassImportLineBase;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * GenericValidation to check duplicate sub account entries within the file and system
 */
public class SubAccountImportEntryDuplicateValidation extends GenericValidation {
    private BusinessObjectService businessObjectService;
    private MassImportLineBase importedLineForValidation;

    /**
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;
        SubAccountImportDocument document = (SubAccountImportDocument) event.getDocument();
        HashMap<String, Object> subAccountsMap = new HashMap<String, Object>();
        String mapKey = null;

        SubAccountImportDetail validatingLine = (SubAccountImportDetail) importedLineForValidation;
        String errorPrefix = KFSConstants.DOCUMENT_PROPERTY_NAME + "." + KFSPropertyConstants.SubAccountImport.SUB_ACCOUNT_IMPORT_DETAILS + "[" + validatingLine.getSequenceNumber() == null ? String.valueOf(0) : String.valueOf(validatingLine.getSequenceNumber().intValue() - 1) + "].";
        String validatingKey = getSubAccountKeyString(validatingLine);
        valid &= checkDuplicatesInFile(document, subAccountsMap, validatingLine, errorPrefix, validatingKey);
        valid &= checkDuplicatesInSystem(validatingLine, errorPrefix);

        return valid;
    }

    private boolean checkDuplicatesInFile(SubAccountImportDocument document, HashMap<String, Object> subAccountsMap, SubAccountImportDetail validatingLine, String errorPrefix, String validatingKey) {
        boolean valid = true;
        String mapKey;
        // set up hash map for the sub-account key and the first line sequence number
        for (SubAccountImportDetail importedLine : document.getSubAccountImportDetails()) {
            mapKey = getSubAccountKeyString(importedLine);
            if (!subAccountsMap.containsKey(mapKey)) {
                subAccountsMap.put(mapKey, importedLine.getSequenceNumber());
            }
            if (subAccountsMap.containsKey(validatingKey) && !subAccountsMap.get(validatingKey).equals(validatingLine.getSequenceNumber())) {
                // find a duplicate sub-account in the same file
                GlobalVariables.getMessageMap().putError(errorPrefix + KFSPropertyConstants.SubAccountImport.CHART_OF_ACCOUNTS_CODE, KFSKeyConstants.ERROR_MASSIMPORT_DUPLICATEENTRYINFILE, new String[] { validatingLine.getSequenceNumber() == null ? "" : validatingLine.getSequenceNumber().toString(), KFSConstants.SubAccountImportConstants.SUB_ACCOUNT });
                valid = false;
                break;
            }
        }
        return valid;
    }

    private boolean checkDuplicatesInSystem(SubAccountImportDetail validatingLine, String errorPrefix) {
        boolean valid = true;
        // check duplicate in BO table
        Map<String, String> primaryKeys = new HashMap<String, String>();
        primaryKeys.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, validatingLine.getChartOfAccountsCode());
        primaryKeys.put(KFSPropertyConstants.ACCOUNT_NUMBER, validatingLine.getAccountNumber());
        primaryKeys.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, validatingLine.getSubAccountNumber());
        SubAccount existingSubAccount = businessObjectService.findByPrimaryKey(SubAccount.class, primaryKeys);

        if (ObjectUtils.isNotNull(existingSubAccount)) {
            GlobalVariables.getMessageMap().putError(errorPrefix + KFSPropertyConstants.SubAccountImport.CHART_OF_ACCOUNTS_CODE, KFSKeyConstants.ERROR_MASSIMPORT_DUPLICATEENTRYINTABLE, new String[] { validatingLine.getSequenceNumber() == null ? "" : validatingLine.getSequenceNumber().toString(), KFSConstants.SubAccountImportConstants.SUB_ACCOUNT });
            valid = false;
        }
        return valid;
    }

    /**
     * Get the primary key string connected by hyphen for given business object
     *
     * @param importedLine
     * @return
     */
    protected String getSubAccountKeyString(SubAccountImportDetail importedLine) {
        return importedLine.getChartOfAccountsCode() + "-" + importedLine.getAccountNumber() + "-" + importedLine.getSubAccountNumber();
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
