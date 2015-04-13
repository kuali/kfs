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
package org.kuali.kfs.module.ld.businessobject;

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Error Certification business object for Salary Expense Transfer and Year End Salary Expense Transfer
 */
public class ErrorCertification extends PersistableBusinessObjectBase {
    private String documentNumber;
    private String expenditureDescription;
    private String expenditureProjectBenefit;
    private String errorDescription;
    private String errorCorrectionReason;

    /**
     * Gets the document number of the document this error certification is associated with.
     *
     * @return Returns the documentNumber.
     */

    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the document number of the document this error certification is associated with.
     *
     * @param documentNumber The document number of the document this error certification is associated with.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * Gets the expenditureDescription attribute.
     *
     * @return Returns the expenditureDescription.
     */
    public String getExpenditureDescription() {
        return expenditureDescription;
    }

    /**
     * Sets the expenditureDescription attribute value.
     *
     * @param expenditureDescription The expenditureDescription to set.
     */
    public void setExpenditureDescription(String expenditureDescription) {
        this.expenditureDescription = expenditureDescription;
    }

    /**
     * Gets the expenditureProjectBenefit attribute.
     *
     * @return Returns the expenditureProjectBenefit.
     */
    public String getExpenditureProjectBenefit() {
        return expenditureProjectBenefit;
    }

    /**
     * Sets the expenditureProjectBenefit attribute value.
     *
     * @param expenditureProjectBenefit The expenditureProjectBenefit to set.
     */
    public void setExpenditureProjectBenefit(String expenditureProjectBenefit) {
        this.expenditureProjectBenefit = expenditureProjectBenefit;
    }

    /**
     * Gets the errorDescription attribute.
     *
     * @return Returns the errorDescription.
     */
    public String getErrorDescription() {
        return errorDescription;
    }

    /**
     * Sets the errorDescription attribute value.
     *
     * @param errorDescription The errorDescription to set.
     */
    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    /**
     * Gets the errorCorrectionReason attribute.
     *
     * @return Returns the errorCorrectionReason.
     */
    public String getErrorCorrectionReason() {
        return errorCorrectionReason;
    }

    /**
     * Sets the errorCorrectionReason attribute value.
     *
     * @param errorCorrectionReason The errorCorrectionReason to set.
     */
    public void setErrorCorrectionReason(String errorCorrectionReason) {
        this.errorCorrectionReason = errorCorrectionReason;
    }
}
