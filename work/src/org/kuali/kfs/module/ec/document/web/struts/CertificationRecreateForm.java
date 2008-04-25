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
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.service.DictionaryValidationService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.effort.EffortConstants;
import org.kuali.module.effort.EffortPropertyConstants;
import org.kuali.module.effort.document.EffortCertificationDocument;

/**
 * To define an action form for effrot certification recreate process
 */
public class CertificationRecreateForm extends EffortCertificationForm {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CertificationRecreateForm.class);

    private String emplid;
    private String personName;
    private Integer universityFiscalYear;
    private String effortCertificationReportNumber;

    /**
     * Constructs a CertificationRecreateForm.java.
     */
    public CertificationRecreateForm() {
        super();
    }

    /**
     * Gets the importing field values.
     * 
     * @return Returns the importing field values.
     */
    private List<String> getImportingFields() {
        List<String> importingFields = new ArrayList<String>();

        importingFields.add(KFSPropertyConstants.EMPLID);
        importingFields.add(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        importingFields.add(EffortPropertyConstants.EFFORT_CERTIFICATION_REPORT_NUMBER);

        return importingFields;
    }

    /**
     * validate the importing field values
     * 
     * @return true if the importing field values are valid; otherwsie, add errors into error map and return false
     */
    public boolean validateImportingFieldValues(EffortCertificationDocument document) {
        DictionaryValidationService dictionaryValidationService = SpringContext.getBean(DictionaryValidationService.class);

        for (String fieldName : this.getImportingFields()) {
            dictionaryValidationService.validateDocumentAttribute(document, fieldName, EffortConstants.DOCUMENT_PREFIX);
        }

        return GlobalVariables.getErrorMap().isEmpty();
    }

    /**
     * force the input data as upper case
     */
    public void forceInputAsUpperCase() {

        String reportNumber = this.getEffortCertificationReportNumber();
        this.setEffortCertificationReportNumber(StringUtils.upperCase(reportNumber));
    }

    /**
     * Gets the emplid attribute.
     * 
     * @return Returns the emplid.
     */
    public String getEmplid() {
        return emplid;
    }

    /**
     * Sets the emplid attribute value.
     * 
     * @param emplid The emplid to set.
     */
    public void setEmplid(String emplid) {
        this.emplid = emplid;
    }

    /**
     * Gets the universityFiscalYear attribute.
     * 
     * @return Returns the universityFiscalYear.
     */
    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    /**
     * Sets the universityFiscalYear attribute value.
     * 
     * @param universityFiscalYear The universityFiscalYear to set.
     */
    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }

    /**
     * Gets the effortCertificationReportNumber attribute.
     * 
     * @return Returns the effortCertificationReportNumber.
     */
    public String getEffortCertificationReportNumber() {
        return effortCertificationReportNumber;
    }

    /**
     * Sets the effortCertificationReportNumber attribute value.
     * 
     * @param effortCertificationReportNumber The effortCertificationReportNumber to set.
     */
    public void setEffortCertificationReportNumber(String effortCertificationReportNumber) {
        this.effortCertificationReportNumber = effortCertificationReportNumber;
    }

    /**
     * Gets the personName attribute.
     * 
     * @return Returns the personName.
     */
    public String getPersonName() {
        return personName;
    }

    /**
     * Sets the personName attribute value.
     * 
     * @param personName The personName to set.
     */
    public void setPersonName(String personName) {
        this.personName = personName;
    }
}
