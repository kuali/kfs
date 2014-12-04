/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.ec.document.web.struts;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ec.EffortConstants;
import org.kuali.kfs.module.ec.EffortPropertyConstants;
import org.kuali.kfs.module.ec.document.EffortCertificationDocument;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.DictionaryValidationService;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * To define an action form for effrot certification recreate process
 */
public class CertificationRecreateForm extends EffortCertificationForm {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CertificationRecreateForm.class);

    protected String emplid;
    protected String personName;
    protected Integer universityFiscalYear;
    protected String effortCertificationReportNumber;

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
    protected List<String> getImportingFields() {
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

        return !GlobalVariables.getMessageMap().hasErrors();
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
    public String getName() {
        return personName;
    }

    /**
     * Sets the personName attribute value.
     * 
     * @param personName The personName to set.
     */
    public void setName(String personName) {
        this.personName = personName;
    }
}

