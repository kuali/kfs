/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.businessobject;

import java.sql.Date;

import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.BusinessObject;

/**
 * Other Expense
 *
 */
public interface OtherExpense extends BusinessObject {

    public Long getId();

    public void setId(final Long id);

    public String getDocumentNumber();

    public void setDocumentNumber(String documentNumber);

    public Integer getDocumentLineNumber();

    public void setDocumentLineNumber(Integer documentLineNumber);

    public Long getExpenseParentId();

    public void setExpenseParentId(Long expenseParentId);

    public Date getExpenseDate();

    public void setExpenseDate(Date expenseDate);

    /**
     * Gets the value of nonReimbursable
     *
     * @return the value of nonReimbursable
     */
    public Boolean getNonReimbursable();

    /**
     * Sets the value of nonReimbursable
     *
     * @param argNonReimbursable Value to assign to this.nonReimbursable
     */
    public void setNonReimbursable(final Boolean argNonReimbursable);

    public KualiDecimal getExpenseAmount();

    public void setExpenseAmount(KualiDecimal expenseAmount);

    public KualiDecimal getConvertedAmount();

    public void setConvertedAmount(KualiDecimal convertedAmount);

    /**
     * Gets the value of travelExpenseTypeCodeCode
     *
     * @return the value of travelExpenseTypeCodeCode
     */
    String getTravelExpenseTypeCodeCode();

    /**
     * Gets the value of travelExpenseTypeCode
     *
     * @return the value of travelExpenseTypeCode
     */
    TemTravelExpenseTypeCode getTravelExpenseTypeCode();

    /**
     * Sets the value of travelExpenseTypeCode
     *
     * @param argTravelExpenseTypeCode Value to assign to this.travelExpenseTypeCode
     */
    void setTravelExpenseTypeCode(final TemTravelExpenseTypeCode argTravelExpenseTypeCode);

    String getSequenceName();

    void setAirfareSourceCode(final String airfareSourceCode);
    String getAirfareSourceCode();

    void setClassOfServiceCode(final String classOfServiceCode);
    String getClassOfServiceCode();

    void setMileageRateId(final Integer mileageRateId);
    Integer getMileageRateId();

    void setMileageRate(final MileageRate mileageRate);
    MileageRate getMileageRate();

    void setMiles(final Integer miles);
    Integer getMiles();

    void setMileageOtherRate(KualiDecimal mileageOtherRate);
    KualiDecimal getMileageOtherRate();

    void setRentalCarInsurance(final Boolean rentalCarInsurance);
    Boolean getRentalCarInsurance();

    void setTaxable(final Boolean taxable);

}
