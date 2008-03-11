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
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.RiceKeyConstants;
import org.kuali.core.document.authorization.DocumentActionFlags;
import org.kuali.core.inquiry.Inquirable;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.SubAccount;
import org.kuali.module.effort.EffortConstants;
import org.kuali.module.effort.EffortPropertyConstants;
import org.kuali.module.effort.bo.EffortCertificationDetail;
import org.kuali.module.effort.document.EffortCertificationDocument;
import org.kuali.module.effort.inquiry.EffortLedgerBalanceInquirableImpl;
import org.kuali.module.effort.util.PayrollAmountHolder;

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
     * validate the importing field values
     * 
     * @return true if the importing field values are valid; otherwsie, add errors into error map and return false
     */
    public boolean validateImporingFieldValues() {
        DataDictionaryService dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);

        Map<String, String> fieldValues = this.getImportingFieldValues();
        for (String fieldName : fieldValues.keySet()) {
            String fieldValue = fieldValues.get(fieldName);

            String fieldLabel = dataDictionaryService.getAttributeLabel(EffortCertificationDocument.class, fieldName);
            boolean isRequired = dataDictionaryService.isAttributeRequired(EffortCertificationDocument.class, fieldName);

            if (isRequired && StringUtils.isBlank(fieldValue)) {
                GlobalVariables.getErrorMap().putError(EffortConstants.DOCUMENT_PREFIX + fieldName, RiceKeyConstants.ERROR_REQUIRED, fieldLabel);
            }
        }

        return GlobalVariables.getErrorMap().isEmpty();
    }

    /**
     * force the input data as upper case
     */
    public void forceInputAsUpperCase() {

        String reportNumber = this.getEffortCertificationDocument().getEffortCertificationReportNumber();
        this.getEffortCertificationDocument().setEffortCertificationReportNumber(StringUtils.upperCase(reportNumber));
    }
}
