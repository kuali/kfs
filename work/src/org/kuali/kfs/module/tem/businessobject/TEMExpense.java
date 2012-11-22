/*
 * Copyright 2011 The Kuali Foundation.
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
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.kuali.kfs.fp.businessobject.TravelCompanyCode;
import org.kuali.rice.core.api.util.type.KualiDecimal;


public interface TEMExpense {

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
     * Gets the value of travelExpenseTypeCodeCode
     *
     * @return the value of travelExpenseTypeCodeCode
     */
    @Column(name = "DV_EXP_CD", nullable = false)
    public String getTravelExpenseTypeCodeCode();

    /**
     * Gets the value of travelExpenseTypeCode
     *
     * @return the value of travelExpenseTypeCode
     */
    @ManyToOne
    @JoinColumn(name = "DV_EXP_CD", nullable = false)
    public TemTravelExpenseTypeCode getTravelExpenseTypeCode();

    /**
     * Sets the value of travelExpenseTypeCode
     *
     * @param argTravelExpenseTypeCode Value to assign to this.travelExpenseTypeCode
     */
    public void setTravelExpenseTypeCode(final TemTravelExpenseTypeCode argTravelExpenseTypeCode);

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
    public KualiDecimal getCurrencyRate();

    /**
     * Sets the value of currencyRate
     *
     * @param argCurrencyRate Value to assign to this.currencyRate
     */
    public void setCurrencyRate(final KualiDecimal argCurrencyRate);

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
    public List<? extends TEMExpense> getExpenseDetails();

    /**
     * Sets the expenseDetails attribute value.
     * @param expenseDetails The expenseDetails to set.
     */
    public void setExpenseDetails(List<TEMExpense> expenseDetails);

    /**
     * Get the total detail expenses amount
     *
     * @return
     */
    public KualiDecimal getTotalDetailExpenseAmount();

    /**
     * @param expense
     */
    public void addExpenseDetails(TEMExpense expense);

    public String getSequenceName();

    public String getTemExpenseTypeCode();

    public void setTemExpenseTypeCode(String temExpenseTypeCode);

    public String getClassOfServiceCode();

    public boolean isRentalCar();

    public Boolean getRentalCarInsurance();

    public String getTravelCompanyCodeCode();

    public Long getTravelExpenseTypeCodeId();

    public void setTravelExpenseTypeCodeId(Long travelExpenseTypeCodeId);
}