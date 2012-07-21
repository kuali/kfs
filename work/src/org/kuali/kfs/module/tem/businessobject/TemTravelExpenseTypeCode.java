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

import javax.persistence.Transient;

import org.kuali.rice.kns.bo.Inactivateable;
import org.kuali.rice.kns.bo.KualiCodeBase;
import org.kuali.rice.kns.util.KualiDecimal;

public class TemTravelExpenseTypeCode extends KualiCodeBase implements Inactivateable{
    private Long travelExpenseTypeCodeId;
    private boolean prepaidExpense;
    private String financialObjectCode;
    private Boolean hostedMeal = Boolean.FALSE;
    private String longDescription;
    private Boolean individual = Boolean.FALSE;
    private Boolean groupTravel = Boolean.FALSE;
    private KualiDecimal maximumAmount;
    private String maximumAmountPer;
    private String errorTypeCode;
    private Boolean noteRequired = Boolean.FALSE;
    private Boolean receiptRequired = Boolean.FALSE;
    private Integer receiptRequirementThreshold;
    private Boolean taxable = Boolean.FALSE;
    private Boolean receiptDisplayRequired = Boolean.FALSE;
    private Boolean specialRequestRequired = Boolean.FALSE;
    
    private String tripType;
    private String travelerType;
    private String documentType;
    
    /**
     * Default no-arg constructor.
     */
    public TemTravelExpenseTypeCode() {

    }

    /**
     * @return Returns the prepaidExpense.
     */
    public boolean isPrepaidExpense() {
        return prepaidExpense;
    }

    /**
     * @param prepaidExpense The prepaidExpense to set.
     */
    public void setPrepaidExpense(boolean prepaidExpense) {
        this.prepaidExpense = prepaidExpense;
    }

    public String getFinancialObjectCode() {
        return financialObjectCode;
    }

    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }

    public Boolean getHostedMeal() {
        return hostedMeal;
    }

    public void setHostedMeal(Boolean hostedMeal) {
        this.hostedMeal = hostedMeal;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public Boolean getIndividual() {
        return individual != null ? individual : false;
    }

    public void setIndividual(Boolean individual) {
        this.individual = individual;
    }

    public Boolean getGroupTravel() {
        return groupTravel != null ? groupTravel : false;
    }

    public void setGroupTravel(Boolean groupTravel) {
        this.groupTravel = groupTravel;
    }

    public KualiDecimal getMaximumAmount() {
        return maximumAmount;
    }

    public void setMaximumAmount(KualiDecimal maximumAmount) {
        this.maximumAmount = maximumAmount;
    }

    public String getMaximumAmountPer() {
        return maximumAmountPer;
    }

    public void setMaximumAmountPer(String maximumAmountPer) {
        this.maximumAmountPer = maximumAmountPer;
    }

    public String getErrorTypeCode() {
        return errorTypeCode;
    }

    public void setErrorTypeCode(String errorTypeCode) {
        this.errorTypeCode = errorTypeCode;
    }

    public Boolean getNoteRequired() {
        return noteRequired;
    }

    public void setNoteRequired(Boolean noteRequired) {
        this.noteRequired = noteRequired;
    }

    public Boolean getReceiptRequired() {
        return receiptRequired != null ? receiptRequired : false;
    }

    public void setReceiptRequired(Boolean receiptRequired) {
        this.receiptRequired = receiptRequired;
    }

    public Integer getReceiptRequirementThreshold() {
        return receiptRequirementThreshold;
    }

    public void setReceiptRequirementThreshold(Integer receiptRequirementThreshold) {
        this.receiptRequirementThreshold = receiptRequirementThreshold;
    }

    public Boolean getTaxable() {
        return taxable;
    }

    public void setTaxable(Boolean taxable) {
        this.taxable = taxable;
    }

    public Boolean getReceiptDisplayRequired() {
        return receiptDisplayRequired;
    }

    public void setReceiptDisplayRequired(Boolean receiptDisplayRequired) {
        this.receiptDisplayRequired = receiptDisplayRequired;
    }

    public Boolean getSpecialRequestRequired() {
        return specialRequestRequired;
    }

    public void setSpecialRequestRequired(Boolean specialRequestRequired) {
        this.specialRequestRequired = specialRequestRequired;
    }

    public String getTripType() {
        return tripType;
    }

    public void setTripType(String tripType) {
        this.tripType = tripType;
    }

    public String getTravelerType() {
        return travelerType;
    }

    public void setTravelerType(String travelerType) {
        this.travelerType = travelerType;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }
    
    @Transient    
    public Boolean isPerDaily(){
        if(this.maximumAmountPer != null && this.maximumAmountPer.equals("D")){
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
    
    @Transient
    public Boolean isPerOccurrence(){
        if(this.maximumAmountPer != null && this.maximumAmountPer.equals("O")){
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public Long getTravelExpenseTypeCodeId() {
        return travelExpenseTypeCodeId;
    }

    public void setTravelExpenseTypeCodeId(Long travelExpenseTypeCodeId) {
        this.travelExpenseTypeCodeId = travelExpenseTypeCodeId;
    }
}
