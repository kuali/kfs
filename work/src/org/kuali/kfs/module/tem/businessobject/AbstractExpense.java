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

import static org.kuali.kfs.module.tem.util.BufferedLogger.debug;
import static org.kuali.kfs.module.tem.util.BufferedLogger.error;
import static org.kuali.kfs.module.tem.util.BufferedLogger.logger;

import java.lang.reflect.Field;
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

import org.kuali.kfs.fp.businessobject.TravelCompanyCode;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.document.TravelDocumentBase;
import org.kuali.kfs.module.tem.service.TravelExpenseService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.util.KualiDecimal;

@Entity
@Table(name="tem_trvl_exp_t")
public abstract class AbstractExpense extends PersistableBusinessObjectBase implements TEMExpense {
    @GeneratedValue(generator="tem_trvl_exp_id_seq")
    @SequenceGenerator(name="tem_trvl_exp_id_seq",sequenceName="tem_trvl_exp_id_seq", allocationSize=5)
    private Long id;    
    private String documentNumber;
    private Integer documentLineNumber;
    private Date expenseDate;
    private KualiDecimal expenseAmount = new KualiDecimal(0.00);
    private Boolean nonReimbursable = Boolean.FALSE;
    private Long travelExpenseTypeCodeId;    
    private TemTravelExpenseTypeCode travelExpenseTypeCode;
    private Long expenseParentId;
    private String description;
    private KualiDecimal currencyRate = new KualiDecimal(1.00);
    private String travelCompanyCodeName;    
    private TravelCompanyCode travelCompanyCode;
    private String travelCompanyCodeCode;
    private Boolean taxable = Boolean.FALSE;
    private Boolean missingReceipt = Boolean.FALSE;
    private KualiDecimal convertedAmount;
    private String temExpenseTypeCode = "";
    private List<TEMExpense> expenseDetails = new ArrayList<TEMExpense>();
    
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

    /**
     * Gets the value of travelExpenseTypeCodeCode
     *
     * @return the value of travelExpenseTypeCodeCode
     */
    @Override
    public String getTravelExpenseTypeCodeCode() {
        TemTravelExpenseTypeCode ttetc = getTravelExpenseTypeCode();
        if (ttetc != null) {
            return ttetc.getCode();
        }
        return null;
    }
       
    /**
     * Gets the value of travelExpenseTypeCode
     *
     * @return the value of travelExpenseTypeCode
     */
    @Override
    @ManyToOne
    @JoinColumn(name="DV_EXP_CD",nullable=false)
    public TemTravelExpenseTypeCode getTravelExpenseTypeCode() {
        if (travelExpenseTypeCodeId != null) {
            travelExpenseTypeCode = SpringContext.getBean(TravelExpenseService.class).getExpenseType(travelExpenseTypeCodeId);
        }
        else if (travelExpenseTypeCodeId == null){
            this.travelExpenseTypeCode = null;
        }
        return this.travelExpenseTypeCode;
    }

    /**
     * Sets the value of travelExpenseTypeCode
     *
     * @param argTravelExpenseTypeCode Value to assign to this.travelExpenseTypeCode
     */
    @Override
    public void setTravelExpenseTypeCode(final TemTravelExpenseTypeCode argTravelExpenseTypeCode) {
        this.travelExpenseTypeCode = argTravelExpenseTypeCode;
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
    public KualiDecimal getCurrencyRate() {
        if (currencyRate == null){
            this.currencyRate = new KualiDecimal(1.00);
        }
        return this.currencyRate;
    }

    /**
     * Sets the value of currencyRate
     *
     * @param argCurrencyRate Value to assign to this.currencyRate
     */
    @Override
    public void setCurrencyRate(final KualiDecimal argCurrencyRate) {
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
            calc = getExpenseAmount().multiply(getCurrencyRate());
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
     * Gets the travelCompanyCodeCode attribute. 
     * @return Returns the travelCompanyCodeCode.
     */
    @Override
    public String getTravelCompanyCodeCode() {
        if (travelCompanyCodeCode == null && travelExpenseTypeCode != null){
            travelCompanyCodeCode = travelExpenseTypeCode.getCode();
        }
        else if (travelExpenseTypeCodeId != null && travelCompanyCodeCode == null){
            setTravelCompanyCodeCode(getTravelExpenseTypeCode().getCode());
        }
        return travelCompanyCodeCode;
    }

    /**
     * Sets the travelCompanyCodeCode attribute value.
     * @param travelCompanyCodeCode The travelCompanyCodeCode to set.
     */
    public void setTravelCompanyCodeCode(String travelCompanyCodeCode) {
        this.travelCompanyCodeCode = travelCompanyCodeCode;
        if (travelCompanyCodeCode != null){
            TravelExpenseService service = SpringContext.getBean(TravelExpenseService.class);
            Long id = service.getExpenseTypeId(travelCompanyCodeCode, documentNumber);
            this.travelExpenseTypeCodeId = id;
        }
        else{
            travelExpenseTypeCode = null;
            this.travelExpenseTypeCodeId = null;
        }
    }

    /**
     * Gets the expenseDetails attribute. 
     * @return Returns the expenseDetails.
     */
    @Override
    public List<? extends TEMExpense> getExpenseDetails() {
        if (expenseDetails == null){
            expenseDetails = new ArrayList<TEMExpense>();
        }
        return expenseDetails;
    }
    
    /**
     * Gets the expenseDetails attribute. 
     * @return Returns the expenseDetails.
     */
    @Override
    public void addExpenseDetails(TEMExpense expense) {
        expenseDetails.add(expense);
    }

    /**
     * Sets the expenseDetails attribute value.
     * @param expenseDetails The expenseDetails to set.
     */
    @Override
    public void setExpenseDetails(List<TEMExpense> expenseDetails) {
        this.expenseDetails = expenseDetails;
    }

    @Override
    public String getSequenceName() {
        Class boClass = getClass();
        String retval = "";
        try {
            boolean rethrow = true;
            Exception e = null;
            while (rethrow) {
                debug("Looking for id in ", boClass.getName());
                try {
                    final Field idField = boClass.getDeclaredField("id");
                    final SequenceGenerator sequenceInfo = idField.getAnnotation(SequenceGenerator.class);
                    
                    return sequenceInfo.sequenceName();
                }
                catch (Exception ee) {
                    // ignore and try again
                    debug("Could not find id in ", boClass.getName());
                    
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
            error("Could not get the sequence name for business object ", getClass().getSimpleName());
            error(e.getMessage());
            if (logger().isDebugEnabled()) {
                e.printStackTrace();
            }
        }
        return retval;
    }
    
    @Override
    protected LinkedHashMap toStringMapper() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Long getTravelExpenseTypeCodeId() {
        return travelExpenseTypeCodeId;
    }

    @Override
    public void setTravelExpenseTypeCodeId(Long travelExpenseTypeCodeId) {
        this.travelExpenseTypeCodeId = travelExpenseTypeCodeId;
    }
    
    
}
