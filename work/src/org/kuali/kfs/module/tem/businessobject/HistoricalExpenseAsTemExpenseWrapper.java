/*
 * Copyright 2013 The Kuali Foundation.
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.tem.businessobject;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.businessobject.TravelCompanyCode;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Wrapper of HistoricalTravelExpense that implements TemExpense
 */
public class HistoricalExpenseAsTemExpenseWrapper implements TemExpense {
    protected HistoricalTravelExpense baseExpense;
    protected BusinessObjectService businessObjectService;
    protected ExpenseType expenseType;

    public HistoricalExpenseAsTemExpenseWrapper(HistoricalTravelExpense expense) {
        this.baseExpense = expense;
    }

    /**
     * @return the id of the base HistoricalTravelExpense
     */
    @Override
    public Long getId() {
        return baseExpense.getId();
    }

    /**
     * All setters are ignored
     */
    @Override
    public void setId(Long id) {}

    /**
     * @return the document number of the base expense
     */
    @Override
    public String getDocumentNumber() {
        return baseExpense.getDocumentNumber();
    }

    /**
     * All setters are ignored
     */
    @Override
    public void setDocumentNumber(String documentNumber) {}

    /**
     * @return null; no equivalent for document line number
     */
    @Override
    public Integer getDocumentLineNumber() {
        return null;
    }

    /**
     * All setters are ignored
     */
    @Override
    public void setDocumentLineNumber(Integer documentLineNumber) {}

    /**
     * @return null; no equivalent for parent id
     */
    @Override
    public Long getExpenseParentId() {
        return null;
    }

    /**
     * All setters are ignored
     */
    @Override
    public void setExpenseParentId(Long expenseParentId) {}

    /**
     * @return the expense notification date of the HistoricalTravelExpense
     */
    @Override
    public Date getExpenseDate() {
        return baseExpense.getExpenseNotificationDate();
    }

    /**
     * All setters are ignored
     */
    @Override
    public void setExpenseDate(Date expenseDate) {}

    /**
     * @return the opposite of HistoricalTravelExpense#getReimbursable
     */
    @Override
    public Boolean getNonReimbursable() {
        return !baseExpense.getReimbursable().booleanValue();
    }

    /**
     * All setters are ignored
     */
    @Override
    public void setNonReimbursable(Boolean nonReimbursable) {}

    /**
     * @return false, as there is no equivalency
     */
    @Override
    public Boolean getTaxable() {
        return Boolean.FALSE;
    }

    /**
     * All setters are ignored
     */
    @Override
    public void setTaxable(Boolean argTaxable) {}

    /**
     * @return missingReciept from the base HistoricalTravelExpense
     */
    @Override
    public Boolean getMissingReceipt() {
        return baseExpense.getMissingReceipt();
    }

    /**
     * All setters are ignored
     */
    @Override
    public void setMissingReceipt(Boolean argMissingReceipt) {}

    /**
     * @return the of the base HistoricalTravelExpense
     */
    @Override
    public KualiDecimal getExpenseAmount() {
        return baseExpense.getAmount();
    }

    /**
     * All setters are ignored
     */
    @Override
    public void setExpenseAmount(KualiDecimal expenseAmount) {}

    /**
     * @return the description of the base HistoricalTravelExpense
     */
    @Override
    public String getDescription() {
        return baseExpense.getDescription();
    }

    /**
     * All setters are ignored
     */
    @Override
    public void setDescription(String argDescription) {}

    /**
     * @return null - no equivalency
     */
    @Override
    public String getNotes() {
        return null;
    }

    /**
     * All setters are ignored
     */
    @Override
    public void setNotes(String argDescription) {}

    /**
     * @return the currencyRate of the base HistoricalTravelExpense
     */
    @Override
    public BigDecimal getCurrencyRate() {
        return baseExpense.getCurrencyRate();
    }

    /**
     * All setters are ignored
     */
    @Override
    public void setCurrencyRate(BigDecimal argCurrencyRate) {}

    /**
     * All setters are ignored
     */
    @Override
    public void setConvertedAmount(KualiDecimal convertedAmount) {}

    /**
     * @return the convertedAmount of the base HistoricalTravelExpense
     */
    @Override
    public KualiDecimal getConvertedAmount() {
        return baseExpense.getConvertedAmount();
    }

    /**
     * @return the travel company of the base HistoricalTravelExpense
     */
    @Override
    public String getTravelCompanyCodeName() {
        return baseExpense.getTravelCompany();
    }

    /**
     * All setters are ignored
     */
    @Override
    public void setTravelCompanyCodeName(String argTravelCompanyCodeName) {}

    /**
     * Returns null - no equivalency
     * @see org.kuali.kfs.module.tem.businessobject.TemExpense#getTravelCompanyCode()
     */
    @Override
    public TravelCompanyCode getTravelCompanyCode() {
        return null;
    }

    /**
     * All setters are ignored
     */
    @Override
    public void setTravelCompanyCode(TravelCompanyCode argTravelCompanyCode) {}

    /**
     * Returns null - no equivalencies
     * @see org.kuali.kfs.module.tem.businessobject.TemExpense#getExpenseDetails()
     */
    @Override
    public List<? extends TemExpense> getExpenseDetails() {
        return null;
    }

    /**
     * All setters are ignored
     */
    @Override
    public void setExpenseDetails(List<TemExpense> expenseDetails) {}

    /**
     * Returns null - no equivalencies
     * @see org.kuali.kfs.module.tem.businessobject.TemExpense#getTotalDetailExpenseAmount()
     */
    @Override
    public KualiDecimal getTotalDetailExpenseAmount() {
        return null;
    }

    /**
     * All adds are ignored
     */
    @Override
    public void addExpenseDetails(TemExpense expense) {}

    /**
     * Returns null - no equivalencies
     * @see org.kuali.kfs.module.tem.businessobject.TemExpense#getSequenceName()
     */
    @Override
    public String getSequenceName() {
        return null;
    }

    /**
     * Returns the travelExpenseType of the base HistoricalTravelExpense
     * @see org.kuali.kfs.module.tem.businessobject.TemExpense#getExpenseLineTypeCode()
     */
    @Override
    public String getExpenseLineTypeCode() {
        return baseExpense.getTravelExpenseType();
    }

    /**
     * All setters are ignored
     */
    @Override
    public void setExpenseLineTypeCode(String expenseLineTypeCode) {}

    /**
     * returns air ticket class from related agency staging data if possible
     * @see org.kuali.kfs.module.tem.businessobject.TemExpense#getClassOfServiceCode()
     */
    @Override
    public String getClassOfServiceCode() {
        if (!ObjectUtils.isNull(baseExpense.getAgencyStagingData())) {
            return baseExpense.getAgencyStagingData().getAirTicketClass();
        }
        return null;
    }

    /**
     * Finds the related expense type to determine if this
     * @see org.kuali.kfs.module.tem.businessobject.TemExpense#isRentalCar()
     */
    @Override
    public boolean isRentalCar() {
       if (getExpenseType() != null) {
           return StringUtils.equals(TemConstants.ExpenseTypeMetaCategory.RENTAL_CAR.getCode(), getExpenseType().getExpenseTypeMetaCategoryCode());
       }
       return false; // no expense type code, so we can't tell...
    }

    /**
     * Always returns false, as it can never be rental car insurance
     * @see org.kuali.kfs.module.tem.businessobject.TemExpense#getRentalCarInsurance()
     */
    @Override
    public Boolean getRentalCarInsurance() {
        return false;
    }

    /**
     * Returns the travelExpenseType of the base HistoricalTravelExpense
     * @see org.kuali.kfs.module.tem.businessobject.TemExpense#getExpenseTypeCode()
     */
    @Override
    public String getExpenseTypeCode() {
        return baseExpense.getTravelExpenseType();
    }

    /**
     * Returns null - no easy equivalency
     */
    @Override
    public Long getExpenseTypeObjectCodeId() {
        return null;
    }

    /**
     * All setters are ignored
     */
    @Override
    public void setExpenseTypeObjectCodeId(Long expenseTypeObjectCodeId) {}

    /**
     * Returns null - no easy equivalency
     * @see org.kuali.kfs.module.tem.businessobject.TemExpense#getExpenseTypeObjectCode()
     */
    @Override
    public ExpenseTypeObjectCode getExpenseTypeObjectCode() {
        return null;
    }

    /**
     * All setters are ignored
     */
    @Override
    public void setExpenseTypeObjectCode(ExpenseTypeObjectCode expenseTypeObjectCode) {}

    /**
     * Does nothing
     */
    @Override
    public void refreshExpenseTypeObjectCode(String documentTypeName, String travelerTypeCode, String tripTypeCode) {}

    public ExpenseType getExpenseType() {
        if (expenseType == null && !StringUtils.isBlank(getExpenseTypeCode())) {
            expenseType = getBusinessObjectService().findBySinglePrimaryKey(ExpenseType.class, getExpenseTypeCode());
        }
        return expenseType;
    }

    public BusinessObjectService getBusinessObjectService() {
        if (businessObjectService == null) {
            businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        }
        return businessObjectService;
    }
}
