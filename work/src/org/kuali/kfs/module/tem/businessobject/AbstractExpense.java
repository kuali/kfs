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

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.log4j.Logger;
import org.kuali.kfs.fp.businessobject.TravelCompanyCode;
import org.kuali.kfs.module.tem.service.TravelExpenseService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

@Entity
@Table(name="tem_trvl_exp_t")
public abstract class AbstractExpense extends PersistableBusinessObjectBase implements TemExpense {

    public static Logger LOG = Logger.getLogger(AbstractExpense.class);

    @GeneratedValue(generator="tem_trvl_exp_id_seq")
    @SequenceGenerator(name="tem_trvl_exp_id_seq",sequenceName="tem_trvl_exp_id_seq", allocationSize=5)
    private Long id;
    private String documentNumber;
    private Integer documentLineNumber;
    private Date expenseDate;
    private KualiDecimal expenseAmount = new KualiDecimal(0.00);
    private Boolean nonReimbursable = Boolean.FALSE;
    private Long expenseTypeObjectCodeId;
    private ExpenseTypeObjectCode expenseTypeObjectCode;
    private ExpenseType expenseType;
    private Long expenseParentId;
    private String description;
    private BigDecimal currencyRate = new BigDecimal(1.00);
    private String travelCompanyCodeName;
    private TravelCompanyCode travelCompanyCode;
    private String expenseTypeCode;
    private Boolean taxable = Boolean.FALSE;
    private Boolean missingReceipt = Boolean.FALSE;
    private KualiDecimal convertedAmount;
    private List<TemExpense> expenseDetails = new ArrayList<TemExpense>();

    @Override
    @Id
    @Column(name="id",nullable=false)
    public Long getId() {
        return id;
    }

    @Override
    public void setId(final Long id) {
        this.id = id;
    }

    @Override
    @Column(name="fdoc_nbr",length=14,nullable=false)
    public String getDocumentNumber() {
        return documentNumber;
    }

    @Override
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    @Override
    @Column(name="fdoc_line_nbr",nullable=false)
    public Integer getDocumentLineNumber() {
        return documentLineNumber;
    }

    @Override
    public void setDocumentLineNumber(Integer documentLineNumber) {
        this.documentLineNumber = documentLineNumber;
    }

    @Override
    @Column(name="exp_parent_id",nullable=true)
    public Long getExpenseParentId() {
        return expenseParentId;
    }

    @Override
    public void setExpenseParentId(Long expenseParentId) {
        this.expenseParentId = expenseParentId;
    }

    @Override
    @Column(name="exp_dt",nullable=true)
    public Date getExpenseDate() {
        return expenseDate;
    }

    @Override
    public void setExpenseDate(Date expenseDate) {
        this.expenseDate = expenseDate;
    }

    /**
     * Gets the value of nonReimbursable
     *
     * @return the value of nonReimbursable
     */
    @Override
    @Column(name="NON_REIM_IND",nullable=true,length=1)
    public Boolean getNonReimbursable() {
        return nonReimbursable != null ? nonReimbursable : false;
    }

    /**
     * Sets the value of nonReimbursable
     *
     * @param argNonReimbursable Value to assign to this.nonReimbursable
     */
    @Override
    public void setNonReimbursable(final Boolean nonReimbursable) {
        this.nonReimbursable = nonReimbursable;
    }

    /**
     * Gets the value of taxable
     *
     * @return the value of taxable
     */
    @Override
    @Column(name="TAXABLE_IND",nullable=true,length=1)
    public Boolean getTaxable() {
        return this.taxable;
    }

    /**
     * Sets the value of taxable
     *
     * @param argTaxable Value to assign to this.taxable
     */
    @Override
    public void setTaxable(final Boolean argTaxable) {
        this.taxable = argTaxable;
    }

    /**
     * Gets the value of missingReceipt
     *
     * @return the value of missingReceipt
     */
    @Override
    @Column(name="MISG_RCPT_IND",nullable=true,length=1)
    public Boolean getMissingReceipt() {
        return this.missingReceipt;
    }

    /**
     * Sets the value of missingReceipt
     *
     * @param argMissingReceipt Value to assign to this.missingReceipt
     */
    @Override
    public void setMissingReceipt(final Boolean argMissingReceipt) {
        this.missingReceipt = argMissingReceipt;
    }


    @Override
    @Column(name="EXP_AMT",precision=19,scale=2,nullable=false)
    public KualiDecimal getExpenseAmount() {
        return expenseAmount;
    }

    @Override
    public void setExpenseAmount(KualiDecimal expenseAmount) {
        this.expenseAmount = expenseAmount;
    }

    @Override
    public Long getExpenseTypeObjectCodeId() {
        return expenseTypeObjectCodeId;
    }

    @Override
    public void setExpenseTypeObjectCodeId(Long expenseTypeObjectCodeId) {
        this.expenseTypeObjectCodeId = expenseTypeObjectCodeId;
    }

    /**
     * Gets the value of expense type object code
     *
     * @return the value of expense type object code
     */
    @Override
    public ExpenseTypeObjectCode getExpenseTypeObjectCode() {
        return this.expenseTypeObjectCode;
    }

    /**
     * Sets the value of the expense type object code
     * @param expenseTypeObjectCode the expense type object code value to set
     * @see org.kuali.kfs.module.tem.businessobject.TemExpense#setExpenseTypeObjectCode(org.kuali.kfs.module.tem.businessobject.ExpenseTypeObjectCode)
     */
    @Override
    public void setExpenseTypeObjectCode(ExpenseTypeObjectCode expenseTypeObjectCode) {
        this.expenseTypeObjectCode = expenseTypeObjectCode;
    }

    /**
     * Sets the value of travelExpenseTypeCode
     *
     * @param argTravelExpenseTypeCode Value to assign to this.travelExpenseTypeCode
     */
    public void setTravelExpenseTypeCode(final ExpenseTypeObjectCode argTravelExpenseTypeCode) {
        this.expenseTypeObjectCode = argTravelExpenseTypeCode;
    }


    /**
     * Gets the value of description
     *
     * @return the value of description
     */
    @Override
    @Column(name="EXP_DESC",length=255,nullable=true)
    public String getDescription() {
        return this.description;
    }

    /**
     * Sets the value of description
     *
     * @param argDescription Value to assign to this.description
     */
    @Override
    public void setDescription(final String argDescription) {
        this.description = argDescription;
    }

    /**
     * Gets the value of description
     *
     * @return the value of description
     */
    @Override
    public String getNotes() {
        return this.description;
    }

    /**
     * Sets the value of description
     *
     * @param argDescription Value to assign to this.description
     */
    @Override
    public void setNotes(final String argDescription) {
        this.description = argDescription;
    }

    /**
     * Gets the value of currencyRate
     *
     * @return the value of currencyRate
     */
    @Override
    @Column(name="CUR_RT",precision=4,scale=3,nullable=false)
    public BigDecimal getCurrencyRate() {
        if (currencyRate == null){
            this.currencyRate = new BigDecimal(1.00);
        }
        return this.currencyRate;
    }

    /**
     * Sets the value of currencyRate
     *
     * @param argCurrencyRate Value to assign to this.currencyRate
     */
    @Override
    public void setCurrencyRate(final BigDecimal argCurrencyRate) {
        this.currencyRate = argCurrencyRate;
    }

    /**
     * Sets the value of convertedAmount
     *
     * @param convertedAmount value to assign to this.convertedAmount
     */
    @Override
    public void setConvertedAmount(final KualiDecimal convertedAmount) {
        this.convertedAmount = convertedAmount;
    }

    /**
     * Get the value of convertedAmount
     *
     * @return the value of convertedAmount
     */
    @Override
    @Column(name="CONVERTED_AMT",precision=7,scale=2,nullable=true)
    public KualiDecimal getConvertedAmount() {
        KualiDecimal calc = KualiDecimal.ZERO;
        if (getExpenseAmount() != null
                && getCurrencyRate() != null){
            calc = new KualiDecimal(getExpenseAmount().bigDecimalValue().multiply(getCurrencyRate()));
        }
        if (!calc.equals(convertedAmount)){
            this.convertedAmount = calc;
        }
        return this.convertedAmount;
    }

    /**
     * Gets the value of travelCompanyCodeName
     *
     * @return the value of travelCompanyCodeName
     */
    @Override
    @Column(name="DV_EXP_CO_NM",nullable=false)
    public String getTravelCompanyCodeName() {
        return this.travelCompanyCodeName;
    }

    /**
     * Sets the value of travelCompanyCodeName
     *
     * @param argTravelCompanyCodeName Value to assign to this.travelCompanyCodeName
     */
    @Override
    public void setTravelCompanyCodeName(final String argTravelCompanyCodeName) {
        this.travelCompanyCodeName = argTravelCompanyCodeName;
    }

    /**
     * Gets the value of travelCompanyCode
     *
     * @return the value of travelCompanyCode
     */
    @Override
    @ManyToOne
    @JoinColumn(name="DV_EXP_CO_NM",nullable=false)
    public TravelCompanyCode getTravelCompanyCode() {
        return this.travelCompanyCode;
    }

    /**
     * Sets the value of travelCompanyCode
     *
     * @param argTravelCompanyCode Value to assign to this.travelCompanyCode
     */
    @Override
    public void setTravelCompanyCode(final TravelCompanyCode argTravelCompanyCode) {
        this.travelCompanyCode = argTravelCompanyCode;
    }

    /**
     * Gets the expenseTypeCode attribute.
     * @return Returns the expenseTypeCode.
     */
    @Override
    public String getExpenseTypeCode() {
        return expenseTypeCode;
    }

    /**
     * Sets the expenseTypeCode attribute value.
     * @param expenseTypeCode The expenseTypeCode to set.
     */
    public void setExpenseTypeCode(String travelCompanyCodeCode) {
        this.expenseTypeCode = travelCompanyCodeCode;
    }

    /**
     *
     * @see org.kuali.kfs.module.tem.businessobject.TemExpense#refreshExpenseTypeObjectCode(java.lang.String, java.lang.String)
     */
    @Override
    public void refreshExpenseTypeObjectCode(String documentTypeName, String travelerTypeCode, String tripTypeCode) {
        final ExpenseTypeObjectCode expenseTypeObjectCode = SpringContext.getBean(TravelExpenseService.class).getExpenseType(expenseTypeCode, documentTypeName, tripTypeCode, travelerTypeCode);
        if (expenseTypeObjectCode != null) {
            this.expenseTypeObjectCodeId = expenseTypeObjectCode.getExpenseTypeObjectCodeId();
            this.expenseTypeObjectCode = expenseTypeObjectCode;

            // and set this on details
            for (TemExpense detail : getExpenseDetails()) {
                detail.setExpenseTypeObjectCodeId(expenseTypeObjectCode.getExpenseTypeObjectCodeId());
                detail.setExpenseTypeObjectCode(expenseTypeObjectCode);
            }
        }
    }

    /**
     * Gets the expenseDetails attribute.
     * @return Returns the expenseDetails.
     */
    @Override
    public List<? extends TemExpense> getExpenseDetails() {
        if (expenseDetails == null){
            expenseDetails = new ArrayList<TemExpense>();
        }
        return expenseDetails;
    }

    /**
     * Gets the expenseDetails attribute.
     * @return Returns the expenseDetails.
     */
    @Override
    public void addExpenseDetails(TemExpense expense) {
        expenseDetails.add(expense);
    }

    /**
     * Sets the expenseDetails attribute value.
     * @param expenseDetails The expenseDetails to set.
     */
    @Override
    public void setExpenseDetails(List<TemExpense> expenseDetails) {
        this.expenseDetails = expenseDetails;
    }

    /**
     * @return the expense type associated with this expense.  expenseTypeCode is not persisted, so we first look at what is persisted:
     * expenseTypeObjectCode; then it tries to pull back via the expenseTypeCode
     */
    public ExpenseType getExpenseType() {
        return expenseType;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public String getSequenceName() {
        Class boClass = getClass();
        String retval = "";
        try {
            boolean rethrow = true;
            Exception e = null;
            while (rethrow) {
                LOG.debug("Looking for id in "+ boClass.getName());
                try {
                    final Field idField = boClass.getDeclaredField("id");
                    final SequenceGenerator sequenceInfo = idField.getAnnotation(SequenceGenerator.class);

                    return sequenceInfo.sequenceName();
                }
                catch (Exception ee) {
                    // ignore and try again
                    LOG.debug("Could not find id in "+ boClass.getName());

                    // At the end. Went all the way up the hierarchy until we got to Object
                    if (Object.class.equals(boClass)) {
                        rethrow = false;
                    }

                    // get the next superclass
                    boClass = boClass.getSuperclass();
                    e = ee;
                }
            }

            if (e != null) {
                throw e;
            }
        }
        catch (Exception e) {
            LOG.error("Could not get the sequence name for business object " + getClass().getSimpleName());
            LOG.error(e.getMessage());
            if (LOG.isDebugEnabled()) {
                e.printStackTrace();
            }
        }
        return retval;
    }

    @SuppressWarnings("rawtypes")
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        return null;
    }

    /**
     * @see org.kuali.kfs.module.tem.businessobject.TemExpense#getTotalDetailExpenseAmount()
     */
    @Override
    public KualiDecimal getTotalDetailExpenseAmount() {
        KualiDecimal totalDetailExpenseAmount = KualiDecimal.ZERO;
        for(TemExpense expense: getExpenseDetails()){
            totalDetailExpenseAmount = totalDetailExpenseAmount.add(expense.getExpenseAmount());
        }
        return totalDetailExpenseAmount;
    }

}
