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

import java.util.LinkedHashMap;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemParameterConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;

@Entity
@Table(name="TEM_TRVL_EXP_T")
public class ImportedExpense extends AbstractExpense implements TemExpense, ExpenseTypeAware {

    private String cardType = "";
    private Boolean receiptRequired = Boolean.FALSE;
    private String temExpenseTypeCode = TemConstants.EXPENSE_IMPORTED;
    private Long historicalTravelExpenseId;
    private Boolean enableNonReimbursable = Boolean.TRUE;
    private String expenseLineTypeCode = TemConstants.EXPENSE_IMPORTED;

    private HistoricalTravelExpense historicalTravelExpense;

    public ImportedExpense(){
    }

    @Override
    public String getExpenseLineTypeCode(){
        return expenseLineTypeCode;
    }

    /**
     * Gets the historicalTravelExpenseId attribute.
     * @return Returns the historicalTravelExpenseId.
     */
    public Long getHistoricalTravelExpenseId() {
        return historicalTravelExpenseId;
    }

    /**
     * Sets the historicalTravelExpenseId attribute value.
     * @param historicalTravelExpenseId The historicalTravelExpenseId to set.
     */
    public void setHistoricalTravelExpenseId(Long historicalTravelExpenseId) {
        this.historicalTravelExpenseId = historicalTravelExpenseId;
    }

    /**
     * Gets the cardType attribute.
     * @return Returns the cardType.
     */
    public String getCardType() {
        return cardType;
    }

    /**
     * Sets the cardType attribute value.
     * @param cardType The cardType to set.
     */
    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    /**
     * Gets the missingReceipt attribute.
     * @return Returns the missingReceipt.
     */
    public Boolean getReceiptRequired() {
        return receiptRequired;
    }

    /**
     * Sets the missingReceipt attribute value.
     * @param missingReceipt The missingReceipt to set.
     */
    public void setReceiptRequired(Boolean receiptRequired) {
        this.receiptRequired = receiptRequired;
    }

    /**
     * Gets the enableNonReimburable attribute.
     * @return Returns the enableNonReimburable.
     */
    public Boolean getEnableNonReimbursable() {
        String cardTypes  = SpringContext.getBean(ParameterService.class).getParameterValueAsString(TemParameterConstants.TEM_DOCUMENT.class, TemConstants.TravelParameters.ALWAYS_REIMBURSABLE_CARD_TYPE);
        if (cardTypes != null) {
            String[] cards = cardTypes.split(",");
            for (String cardStr : cards){
                if (getCardType() != null && getCardType().equalsIgnoreCase(cardStr)){
                    enableNonReimbursable = false;
                }
            }
        }

        return enableNonReimbursable;
    }

    /**
     * Sets the enableNonReimburable attribute value.
     * @param enableNonReimburable The enableNonReimburable to set.
     */
    public void setEnableNonReimbursable(Boolean enableNonReimbursable) {
        this.enableNonReimbursable = enableNonReimbursable;
    }

    @SuppressWarnings("rawtypes")
    @Override
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        return null;
    }

    @Override
    public void setExpenseLineTypeCode(String expenseLineTypeCode) {
        this.expenseLineTypeCode = expenseLineTypeCode;
    }

    @Override
    public String getClassOfServiceCode() {
        return null;
    }

    @Override
    public boolean isRentalCar() {
        return false;
    }

    @Override
    public Boolean getRentalCarInsurance() {
        return false;
    }

    public HistoricalTravelExpense getHistoricalTravelExpense() {
        return historicalTravelExpense;
    }

    public void setHistoricalTravelExpense(HistoricalTravelExpense historicalTravelExpense) {
        this.historicalTravelExpense = historicalTravelExpense;
    }
}
