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

import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.SubObjectCode;
import org.kuali.kfs.coa.businessobject.SubObjectCodeImportDetail;
import org.kuali.kfs.coa.document.SubObjectCodeImportDocument;
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
 * GenericValidation to check for duplicate Sub Object Code entries within the file and system
 */
public class SubObjectCodeImportEntryDuplicateValidation extends GenericValidation {
    private static final Logger LOG = Logger.getLogger(SubObjectCodeImportEntryDuplicateValidation.class);
    private BusinessObjectService businessObjectService;
    private MassImportLineBase importedLineForValidation;


    /**
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;
        SubObjectCodeImportDocument document = (SubObjectCodeImportDocument) event.getDocument();
        HashMap<String, Object> subObjectCodeMap = new HashMap<String, Object>();
        String mapKey = null;

        SubObjectCodeImportDetail validatingLine = (SubObjectCodeImportDetail) importedLineForValidation;
        String errorPrefix = KFSConstants.DOCUMENT_PROPERTY_NAME + "." + KFSPropertyConstants.SubObjectCodeImport.SUB_OBJECT_CODE_IMPORT_DETAILS + "[" + validatingLine.getSequenceNumber() == null ? String.valueOf(0) : String.valueOf(validatingLine.getSequenceNumber().intValue() - 1) + "].";
        String validatingKey = getSubObjectKeyInString(validatingLine);

        valid &= checkDuplicatesInFile(document, subObjectCodeMap, validatingLine, errorPrefix, validatingKey);

        valid &= checkDuplicatesInSystem(validatingLine, errorPrefix);


        return valid;
    }

    private boolean checkDuplicatesInFile(SubObjectCodeImportDocument document, HashMap<String, Object> subObjectCodeMap, SubObjectCodeImportDetail validatingLine, String errorPrefix, String validatingKey) {
        boolean valid = true;
        String mapKey;
        // set up hash map for the sub-object code key and the first line sequence number
        for (SubObjectCodeImportDetail importedLine : document.getSubObjectCodeImportDetails()) {
            mapKey = getSubObjectKeyInString(importedLine);
            if (!subObjectCodeMap.containsKey(mapKey)) {
                subObjectCodeMap.put(mapKey, importedLine.getSequenceNumber());
            }
            // find a duplicate sub-object code in the same file
            if (subObjectCodeMap.containsKey(validatingKey) && !subObjectCodeMap.get(validatingKey).equals(validatingLine.getSequenceNumber())) {
                GlobalVariables.getMessageMap().putError(errorPrefix + KFSPropertyConstants.SubObjectCodeImport.FIN_SUB_OBJECT_CODE, KFSKeyConstants.ERROR_MASSIMPORT_DUPLICATEENTRYINFILE, new String[] { validatingLine.getSequenceNumber() == null ? "" : validatingLine.getSequenceNumber().toString(), KFSConstants.SubObjectCodeImportConstants.SUB_OBJECT_CODE });
                valid = false;
                break;
            }
        }
        return valid;
    }

    private boolean checkDuplicatesInSystem(SubObjectCodeImportDetail validatingLine, String errorPrefix) {
        Map<String, String> primaryKeys = new HashMap<String, String>();
        boolean valid = true;
        // check duplicate in BO table
        primaryKeys.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, validatingLine.getUniversityFiscalYear().toString());
        primaryKeys.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, validatingLine.getChartOfAccountsCode());
        primaryKeys.put(KFSPropertyConstants.ACCOUNT_NUMBER, validatingLine.getAccountNumber());
        primaryKeys.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, validatingLine.getFinancialObjectCode());
        primaryKeys.put(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE, validatingLine.getFinancialSubObjectCode());
        SubObjectCode existingSubObjectCode = businessObjectService.findByPrimaryKey(SubObjectCode.class, primaryKeys);

        if (ObjectUtils.isNotNull(existingSubObjectCode)) {
            GlobalVariables.getMessageMap().putError(errorPrefix + KFSPropertyConstants.SubObjectCodeImport.FIN_SUB_OBJECT_CODE, KFSKeyConstants.ERROR_MASSIMPORT_DUPLICATEENTRYINTABLE, new String[] { validatingLine.getSequenceNumber() == null ? "" : validatingLine.getSequenceNumber().toString(), KFSConstants.SubObjectCodeImportConstants.SUB_OBJECT_CODE });
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
    protected String getSubObjectKeyInString(SubObjectCodeImportDetail importedLine) {
        return importedLine.getUniversityFiscalYear() + "-" + importedLine.getChartOfAccountsCode() + "-" + importedLine.getAccountNumber() + "-" + importedLine.getFinancialObjectCode() + "-" + importedLine.getFinancialSubObjectCode();
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
     * @return Returns the importedLineForValidation.
     */
    public MassImportLineBase getImportedLineForValidation() {
        return importedLineForValidation;
    }

    /**
     * Sets the importedLineForValidation attribute value.
     * @param importedLineForValidation The importedLineForValidation to set.
     */
    public void setImportedLineForValidation(MassImportLineBase importedLineForValidation) {
        this.importedLineForValidation = importedLineForValidation;
    }

}
