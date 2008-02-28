/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.module.effort.web.struts.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.RiceConstants;
import org.kuali.RiceKeyConstants;
import org.kuali.core.document.authorization.DocumentActionFlags;
import org.kuali.core.inquiry.Inquirable;
import org.kuali.core.service.BusinessObjectDictionaryService;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.effort.EffortConstants;
import org.kuali.module.effort.EffortPropertyConstants;
import org.kuali.module.effort.bo.EffortCertificationDetail;
import org.kuali.module.effort.document.EffortCertificationDocument;

/**
 * To define an action form for effrot certification recreate process
 */
public class CertificationRecreateForm extends EffortCertificationForm {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CertificationRecreateForm.class);

    /**
     * Constructs a CertificationRecreateForm.java.
     */
    public CertificationRecreateForm() {
        super();
    }

    /**
     * Gets the inquiryUrl attribute.
     * 
     * @return Returns the inquiryUrl for the detail lines in the document.
     */
    public List<Map<String, String>> getDetailLineFieldInquiryUrl() {
        Inquirable inquirable = SpringContext.getBean(Inquirable.class);
        List<Map<String, String>> inquiryURL = new ArrayList<Map<String, String>>();

        for (EffortCertificationDetail detailLine : this.getDetailLines()) {
            detailLine.refreshNonUpdateableReferences();
            Map<String, String> inquiryURLForAttribute = new HashMap<String, String>();

            for (String attributeName : this.getInquirableFieldNames()) {
                String url = inquirable.getInquiryUrl(detailLine, attributeName, false);
                inquiryURLForAttribute.put(attributeName, url);
            }

            inquiryURL.add(inquiryURLForAttribute);
        }

        return inquiryURL;
    }

    /**
     * Gets the inquirableFieldNames attribute.
     * 
     * @return Returns the inquirableFieldNames.
     */
    public List<String> getInquirableFieldNames() {
        List<String> inquirableFieldNames = new ArrayList<String>();
        inquirableFieldNames.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        inquirableFieldNames.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        inquirableFieldNames.add(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        inquirableFieldNames.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);

        return inquirableFieldNames;
    }

    /**
     * Gets the inquirableFieldNames attribute.
     * 
     * @return Returns the inquirableFieldNames.
     */
    public Map<String, String> getImportingFieldValues() {
        EffortCertificationDocument document = this.getEffortCertificationDocument();
        String yearAsString = document.getUniversityFiscalYear() == null ? null : document.getUniversityFiscalYear().toString();

        Map<String, String> importingFieldValues = new HashMap<String, String>();
        importingFieldValues.put(KFSPropertyConstants.EMPLID, document.getEmplid());
        importingFieldValues.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, yearAsString);
        importingFieldValues.put(EffortPropertyConstants.EFFORT_CERTIFICATION_REPORT_NUMBER, document.getEffortCertificationReportNumber());

        return importingFieldValues;
    }

    /**
     * Gets the fieldInfo attribute.
     * 
     * @return Returns the fieldInfo.
     */
    public List<Map<String, String>> getFieldInfo() {
        List<Map<String, String>> fieldInfo = new ArrayList<Map<String, String>>();

        for (EffortCertificationDetail detailLine : this.getDetailLines()) {
            detailLine.refreshNonUpdateableReferences();

            Map<String, String> fieldInfoForAttribute = new HashMap<String, String>();

            fieldInfoForAttribute.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, detailLine.getChartOfAccounts().getFinChartOfAccountDescription());
            fieldInfoForAttribute.put(KFSPropertyConstants.ACCOUNT_NUMBER, detailLine.getAccount().getAccountName());

            if (detailLine.getSubAccount() != null) {
                fieldInfoForAttribute.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, detailLine.getSubAccount().getSubAccountName());
            }

            if (detailLine.getFinancialObject() != null) {
                fieldInfoForAttribute.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, detailLine.getFinancialObject().getFinancialObjectCodeName());
            }

            fieldInfo.add(fieldInfoForAttribute);
        }

        return fieldInfo;
    }

    /**
     * @return DocumentActionFlags instance indicating what actions the current user can take on this document
     */
    @Override
    public DocumentActionFlags getDocumentActionFlags() {
        documentActionFlags.setCanClose(false);
        documentActionFlags.setCanBlanketApprove(false);
        documentActionFlags.setHasAmountTotal(true);

        return documentActionFlags;
    }

    /**
     * Gets the detailLines attribute.
     * 
     * @return Returns the detailLines.
     */
    public List<EffortCertificationDetail> getDetailLines() {
        return ((EffortCertificationDocument) this.getDocument()).getEffortCertificationDetailLines();
    }

    /**
     * validate the importing field values
     * 
     * @return true if the importing field values are valid; otherwsie, add errors into error map and return false
     */
    public boolean validateImporingFieldValues() {
        DataDictionaryService dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);

        Map<String, String> fieldValues = this.getImportingFieldValues();
        for (String fieldName : fieldValues.keySet()) {
            String fieldLabel = dataDictionaryService.getAttributeLabel(EffortCertificationDocument.class, fieldName);
            String fieldValue = fieldValues.get(fieldName);

            if (StringUtils.isBlank(fieldValue)) {
                GlobalVariables.getErrorMap().putError(EffortConstants.DOCUMENT_PREFIX + fieldName, RiceKeyConstants.ERROR_REQUIRED, fieldLabel);
            }
        }
        
        return GlobalVariables.getErrorMap().isEmpty();
    }
}
