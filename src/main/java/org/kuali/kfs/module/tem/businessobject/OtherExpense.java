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
package org.kuali.kfs.module.tem.businessobject;

import java.math.BigDecimal;
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
     * Gets the value of travelExpenseTypeCode
     *
     * @return the value of travelExpenseTypeCode
     */
    ExpenseTypeObjectCode getExpenseTypeObjectCode();

    /**
     * Sets the value of travelExpenseTypeCode
     *
     * @param argTravelExpenseTypeCode Value to assign to this.travelExpenseTypeCode
     */
    void setTravelExpenseTypeCode(final ExpenseTypeObjectCode argTravelExpenseTypeCode);

    String getSequenceName();

    void setAirfareSourceCode(final String airfareSourceCode);
    String getAirfareSourceCode();

    void setClassOfServiceCode(final String classOfServiceCode);
    String getClassOfServiceCode();

    void setMiles(final Integer miles);
    Integer getMiles();

    void setMileageOtherRate(BigDecimal mileageOtherRate);
    BigDecimal getMileageOtherRate();

    void setRentalCarInsurance(final Boolean rentalCarInsurance);
    Boolean getRentalCarInsurance();

    void setTaxable(final Boolean taxable);

}
