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
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.kuali.kfs.fp.businessobject.TravelCompanyCode;
import org.kuali.rice.core.api.util.type.KualiDecimal;


public interface TemExpense {

    @Id
    @Column(name = "id", nullable = false)
    public Long getId();

    public void setId(final Long id);

    @Column(name = "fdoc_nbr", length = 14, nullable = false)
    public String getDocumentNumber();

    public void setDocumentNumber(String documentNumber);

    @Column(name = "fdoc_line_nbr", nullable = false)
    public Integer getDocumentLineNumber();

    public void setDocumentLineNumber(Integer documentLineNumber);

    @Column(name = "exp_parent_id", nullable = true)
    public Long getExpenseParentId();

    public void setExpenseParentId(Long expenseParentId);

    @Column(name = "exp_dt", nullable = true)
    public Date getExpenseDate();

    public void setExpenseDate(Date expenseDate);

    /**
     * Gets the value of reimbursable
     *
     * @return the value of reimbursable
     */
    @Column(name = "NON_REIM_IND", nullable = true, length = 1)
    public Boolean getNonReimbursable();

    /**
     * Sets the value of reimbursable
     *
     * @param argReimbursable Value to assign to this.reimbursable
     */
    public void setNonReimbursable(final Boolean nonReimbursable);

    /**
     * Gets the value of taxable
     *
     * @return the value of taxable
     */
    @Column(name = "TAXABLE_IND", nullable = true, length = 1)
    public Boolean getTaxable();

    /**
     * Sets the value of taxable
     *
     * @param argTaxable Value to assign to this.taxable
     */
    public void setTaxable(final Boolean argTaxable);

    /**
     * Gets the value of missingReceipt
     *
     * @return the value of missingReceipt
     */
    @Column(name = "MISG_RCPT_IND", nullable = true, length = 1)
    public Boolean getMissingReceipt();

    /**
     * Sets the value of missingReceipt
     *
     * @param argMissingReceipt Value to assign to this.missingReceipt
     */
    public void setMissingReceipt(final Boolean argMissingReceipt);

    @Column(name = "EXP_AMT", precision = 19, scale = 2, nullable = false)
    public KualiDecimal getExpenseAmount();

    public void setExpenseAmount(KualiDecimal expenseAmount);

    /**
     * Gets the value of description
     *
     * @return the value of description
     */
    @Column(name = "EXP_DESC", length = 255, nullable = true)
    public String getDescription();

    /**
     * Sets the value of description
     *
     * @param argDescription Value to assign to this.description
     */
    public void setDescription(final String argDescription);

    /**
     * Gets the value of description
     *
     * @return the value of description
     */
    public String getNotes();

    /**
     * Sets the value of description
     *
     * @param argDescription Value to assign to this.description
     */
    public void setNotes(final String argDescription);

    /**
     * Gets the value of currencyRate
     *
     * @return the value of currencyRate
     */
    @Column(name = "CUR_RT", precision = 4, scale = 3, nullable = false)
    public BigDecimal getCurrencyRate();

    /**
     * Sets the value of currencyRate
     *
     * @param argCurrencyRate Value to assign to this.currencyRate
     */
    public void setCurrencyRate(final BigDecimal argCurrencyRate);

    /**
     * Sets the value of convertedAmount
     *
     * @param convertedAmount value to assign to this.convertedAmount
     */
    public void setConvertedAmount(final KualiDecimal convertedAmount);

    /**
     * Get the value of convertedAmount
     *
     * @return the value of convertedAmount
     */
    @Column(name = "CONVERTED_AMT", precision = 7, scale = 2, nullable = true)
    public KualiDecimal getConvertedAmount();

    /**
     * Gets the value of travelCompanyCodeName
     *
     * @return the value of travelCompanyCodeName
     */
    @Column(name = "DV_EXP_CO_NM", nullable = false)
    public String getTravelCompanyCodeName();

    /**
     * Sets the value of travelCompanyCodeName
     *
     * @param argTravelCompanyCodeName Value to assign to this.travelCompanyCodeName
     */
    public void setTravelCompanyCodeName(final String argTravelCompanyCodeName);

    /**
     * Gets the value of travelCompanyCode
     *
     * @return the value of travelCompanyCode
     */
    @ManyToOne
    @JoinColumn(name = "DV_EXP_CO_NM", nullable = false)
    public TravelCompanyCode getTravelCompanyCode();

    /**
     * Sets the value of travelCompanyCode
     *
     * @param argTravelCompanyCode Value to assign to this.travelCompanyCode
     */
    public void setTravelCompanyCode(final TravelCompanyCode argTravelCompanyCode);

    /**
     * Gets the expenseDetails attribute.
     * @return Returns the expenseDetails.
     */
    public List<? extends TemExpense> getExpenseDetails();

    /**
     * Sets the expenseDetails attribute value.
     * @param expenseDetails The expenseDetails to set.
     */
    public void setExpenseDetails(List<TemExpense> expenseDetails);

    /**
     * Get the total detail expenses amount
     *
     * @return
     */
    public KualiDecimal getTotalDetailExpenseAmount();

    /**
     * @param expense
     */
    public void addExpenseDetails(TemExpense expense);

    public String getSequenceName();

    public String getExpenseLineTypeCode();

    public void setExpenseLineTypeCode(String expenseLineTypeCode);

    public String getClassOfServiceCode();

    public boolean isRentalCar();

    public Boolean getRentalCarInsurance();

    public String getExpenseTypeCode();

    public Long getExpenseTypeObjectCodeId();

    public void setExpenseTypeObjectCodeId(Long expenseTypeObjectCodeId);

    public ExpenseTypeObjectCode getExpenseTypeObjectCode();

    public void setExpenseTypeObjectCode(ExpenseTypeObjectCode expenseTypeObjectCode);

    /**
     * Requests that this expense refresh its expense type object code (and, by implication, expenseTypeObjectCodeId) based on the values passed in
     * @param documentTypeName the document type name of the document owning this expense
     * @param travelerTypeCode the traveler type code of the traveler associated with the document which owns this expense
     * @param tripCode the trip type code associated with teh document which owns this expense
     */
    public void refreshExpenseTypeObjectCode(String documentTypeName, String travelerTypeCode, String tripTypeCode);
}
