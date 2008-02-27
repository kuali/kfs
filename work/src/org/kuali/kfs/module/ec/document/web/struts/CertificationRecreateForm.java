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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.core.document.authorization.DocumentActionFlags;
import org.kuali.core.inquiry.Inquirable;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.effort.bo.EffortCertificationDetail;
import org.kuali.module.effort.document.EffortCertificationDocument;

public class CertificationRecreateForm extends EffortCertificationForm {

    private Inquirable inquirable = SpringContext.getBean(Inquirable.class);

    /**
     * Constructs a CertificationRecreateForm.java.
     */
    public CertificationRecreateForm() {
        super();
        this.getDocument().refreshNonUpdateableReferences();
    }

    /**
     * Gets the inquiryUrl attribute.
     * 
     * @return Returns the inquiryUrl for the detail lines in the document.
     */
    public List<Map<String, String>> getDetailLineFieldInquiryUrl() {
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
     * @return Returns the detailLines.
     */
    public List<EffortCertificationDetail> getDetailLines() {
        return ((EffortCertificationDocument) this.getDocument()).getEffortCertificationDetailLines();
    }   
}
