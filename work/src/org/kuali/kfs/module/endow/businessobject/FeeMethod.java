/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.businessobject;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.KualiCodeBase;

/**
 * Business Object for Fee Method.
 */
public class FeeMethod extends KualiCodeBase {
    private static final Logger LOG = Logger.getLogger(FeeMethod.class);

    private String feeFrequencyCode;
    private Date feeNextProcessDate;
    private Date feeLastProcessDate;
    private String feeRateDefinitionCode;
    private BigDecimal firstFeeRate;
    private KualiDecimal firstFeeBreakpoint;
    private BigDecimal secondFeeRate;
    private KualiDecimal secondFeeBreakpoint;
    private BigDecimal thirdFeeRate;
    private KualiDecimal minimumFeeThreshold;    
    private KualiDecimal minimumFeeToCharge;
    private String feeTypeCode;
    private String feeBaseCode;
    private String feeExpenseETranCode;
    private boolean feePostPendingIndicator;
    private KualiDecimal corpusPctTolerance;
    private String feeBalanceTypeCode;
    private boolean feeByClassCode;
    private boolean feeBySecurityCode;
    private boolean feeByTransactionType;
    private boolean feeByETranCode;

    private FrequencyCode frequencyCode;
    private FeeRateDefinitionCode feeRateDefinition;
    private EndowmentTransactionCode endowmentTransactionCode;
    private FeeTypeCode feeType;
    private FeeBaseCode feeBase;
    private FeeBalanceTypeCode feeBalanceType;

    // collection classes
    private List<FeeClassCode> feeClassCodes;
    private List<FeeSecurity> feeSecurity;
    private List<FeePaymentType> feePaymentTypes;
    private List<FeeTransaction> feeTransactions;
    private List<FeeEndowmentTransactionCode> feeEndowmentTransactionCodes;

    /**
     * Constructs a TypeCode object
     */
    public FeeMethod() {
        super();
        feeClassCodes = new ArrayList<FeeClassCode>();
        feeSecurity = new ArrayList<FeeSecurity>();
        feePaymentTypes = new ArrayList<FeePaymentType>();
        feeTransactions = new ArrayList<FeeTransaction>();
        feeEndowmentTransactionCodes = new ArrayList<FeeEndowmentTransactionCode>();
    }

    /**
     * Gets the feeFrequencyCode attribute.
     * 
     * @return Returns the feeFrequencyCode.
     */
    public String getFeeFrequencyCode() {
        return feeFrequencyCode;
    }

    /**
     * Sets the feeFrequencyCode attribute value.
     * 
     * @param feeFrequencyCode The feeFrequencyCode to set.
     */
    public void setFeeFrequencyCode(String feeFrequencyCode) {
        this.feeFrequencyCode = feeFrequencyCode;
    }

    /**
     * Gets the feeNextProcessDate attribute.
     * 
     * @return Returns the feeNextProcessDate.
     */
    public Date getFeeNextProcessDate() {
        return feeNextProcessDate;
    }

    /**
     * Sets the feeNextProcessDate attribute value.
     * 
     * @param feeNextProcessDate The feeNextProcessDate to set.
     */
    public void setFeeNextProcessDate(Date feeNextProcessDate) {
        this.feeNextProcessDate = feeNextProcessDate;
    }


    /**
     * Gets the feeLastProcessDate attribute.
     * 
     * @return Returns the feeLastProcessDate.
     */
    public Date getFeeLastProcessDate() {
        return feeLastProcessDate;
    }

    /**
     * Sets the feeLastProcessDate attribute value.
     * 
     * @param feeLastProcessDate The feeLastProcessDate to set.
     */
    public void setFeeLastProcessDate(Date feeLastProcessDate) {
        this.feeLastProcessDate = feeLastProcessDate;
    }

    /**
     * Gets the feeRateDefinitionCode attribute.
     * 
     * @return Returns the feeRateDefinitionCode.
     */
    public String getFeeRateDefinitionCode() {
        return feeRateDefinitionCode;
    }

    /**
     * Sets the feeRateDefinitionCode attribute value.
     * 
     * @param feeRateDefinitionCode The feeRateDefinitionCode to set.
     */
    public void setFeeRateDefinitionCode(String feeRateDefinitionCode) {
        this.feeRateDefinitionCode = feeRateDefinitionCode;
    }

    /**
     * Gets the firstFeeRate attribute.
     * 
     * @return Returns the firstFeeRate.
     */
    public BigDecimal getFirstFeeRate() {
        if (firstFeeRate != null) {
            return firstFeeRate.setScale(EndowConstants.FeeMethod.FEE_RATE_MAX_SCALE, KualiDecimal.ROUND_BEHAVIOR);
        }
        else {
            return BigDecimal.ZERO.setScale(EndowConstants.FeeMethod.FEE_RATE_MAX_SCALE, KualiDecimal.ROUND_BEHAVIOR);
        }
    }

    /**
     * Sets the firstFeeRate attribute value.
     * 
     * @param firstFeeRate The firstFeeRate to set.
     */
    public void setFirstFeeRate(BigDecimal firstFeeRate) {
        this.firstFeeRate = firstFeeRate.setScale(EndowConstants.FeeMethod.FEE_RATE_MAX_SCALE, KualiDecimal.ROUND_BEHAVIOR);
    }

    /**
     * Gets the firstFeeBreakpoint attribute.
     * 
     * @return Returns the firstFeeBreakpoint.
     */
    public KualiDecimal getFirstFeeBreakpoint() {
        return firstFeeBreakpoint;
    }

    /**
     * Sets the firstFeeBreakpoint attribute value.
     * 
     * @param firstFeeBreakpoint The firstFeeBreakpoint to set.
     */
    public void setFirstFeeBreakpoint(KualiDecimal firstFeeBreakpoint) {
        this.firstFeeBreakpoint = firstFeeBreakpoint;
    }

    /**
     * Gets the secondFeeRate attribute.
     * 
     * @return Returns the address2.
     */
    public BigDecimal getSecondFeeRate() {
        if (secondFeeRate != null) {
            return secondFeeRate.setScale(EndowConstants.FeeMethod.FEE_RATE_MAX_SCALE, KualiDecimal.ROUND_BEHAVIOR);
        }
        else {
            return BigDecimal.ZERO.setScale(EndowConstants.FeeMethod.FEE_RATE_MAX_SCALE, KualiDecimal.ROUND_BEHAVIOR);
        }
    }

    /**
     * Sets the secondFeeRate attribute value.
     * 
     * @param secondFeeRate The secondFeeRate to set.
     */
    public void setSecondFeeRate(BigDecimal secondFeeRate) {
        this.secondFeeRate = secondFeeRate.setScale(EndowConstants.FeeMethod.FEE_RATE_MAX_SCALE, KualiDecimal.ROUND_BEHAVIOR);
    }

    /**
     * Gets the secondFeeBreakpoint attribute.
     * 
     * @return Returns the secondFeeBreakpoint.
     */
    public KualiDecimal getSecondFeeBreakpoint() {
        return secondFeeBreakpoint;
    }

    /**
     * Sets the secondFeeBreakpoint attribute value.
     * 
     * @param secondFeeBreakpoint The secondFeeBreakpoint to set.
     */
    public void setSecondFeeBreakpoint(KualiDecimal secondFeeBreakpoint) {
        this.secondFeeBreakpoint = secondFeeBreakpoint;
    }

    /**
     * Gets the thirdFeeRate attribute.
     * 
     * @return Returns the thirdFeeRate.
     */
    public BigDecimal getThirdFeeRate() {
        if (thirdFeeRate != null) {
            return thirdFeeRate.setScale(EndowConstants.FeeMethod.FEE_RATE_MAX_SCALE, KualiDecimal.ROUND_BEHAVIOR);
        }
        else {
            return BigDecimal.ZERO.setScale(EndowConstants.FeeMethod.FEE_RATE_MAX_SCALE, KualiDecimal.ROUND_BEHAVIOR);
        }
    }

    /**
     * Sets the thirdFeeRate attribute value.
     * 
     * @param thirdFeeRate The thirdFeeRate to set.
     */
    public void setThirdFeeRate(BigDecimal thirdFeeRate) {
        this.thirdFeeRate = thirdFeeRate.setScale(EndowConstants.FeeMethod.FEE_RATE_MAX_SCALE, KualiDecimal.ROUND_BEHAVIOR);
    }

    /**
     * Gets the minimumFeeToCharge attribute.
     * 
     * @return Returns the minimumFeeToCharge.
     */
    public KualiDecimal getMinimumFeeToCharge() {
        return minimumFeeToCharge;
    }

    /**
     * Sets the minimumFeeToCharge attribute value.
     * 
     * @param minimumFeeToCharge The minimumFeeToCharge to set.
     */
    public void setMinimumFeeToCharge(KualiDecimal minimumFeeToCharge) {
        this.minimumFeeToCharge = minimumFeeToCharge;
    }

    /**
     * Gets the feeTypeCode attribute.
     * 
     * @return Returns the feeTypeCode.
     */
    public String getFeeTypeCode() {
        return feeTypeCode;
    }

    /**
     * Sets the feeTypeCode attribute value.
     * 
     * @param feeTypeCode The feeTypeCode to set.
     */
    public void setFeeTypeCode(String feeTypeCode) {
        this.feeTypeCode = feeTypeCode;
    }

    /**
     * Gets the feeBaseCode attribute.
     * 
     * @return Returns the feeBaseCode.
     */
    public String getFeeBaseCode() {
        return feeBaseCode;
    }

    /**
     * Sets the feeBaseCode attribute value.
     * 
     * @param feeBaseCode The feeBaseCode to set.
     */
    public void setFeeBaseCode(String feeBaseCode) {
        this.feeBaseCode = feeBaseCode;
    }

    /**
     * Gets the feeExpenseETranCode attribute.
     * 
     * @return Returns the feeExpenseETranCode.
     */
    public String getFeeExpenseETranCode() {
        return feeExpenseETranCode;
    }

    /**
     * Sets the feeExpenseETranCode attribute value.
     * 
     * @param feeExpenseETranCode feeExpenseETranCode feeBaseCode to set.
     */
    public void setFeeExpenseETranCode(String feeExpenseETranCode) {
        this.feeExpenseETranCode = feeExpenseETranCode;
    }

    /**
     * Gets the feePostPendingIndicator attribute.
     * 
     * @return Returns the feePostPendingIndicator.
     */
    public boolean getFeePostPendingIndicator() {
        return feePostPendingIndicator;
    }

    /**
     * Sets the feePostPendingIndicator attribute value.
     * 
     * @param feePostPendingIndicator The feePostPendingIndicator to set.
     */
    public void setFeePostPendingIndicator(boolean feePostPendingIndicator) {
        this.feePostPendingIndicator = feePostPendingIndicator;
    }

    /**
     * Gets the corpusPctTolerance attribute.
     * 
     * @return Returns the corpusPctTolerance.
     */
    public KualiDecimal getCorpusPctTolerance() {
        return corpusPctTolerance;
    }

    /**
     * Sets the corpusPctTolerance attribute value.
     * 
     * @param corpusPctTolerance The corpusPctTolerance to set.
     */
    public void setCorpusPctTolerance(KualiDecimal corpusPctTolerance) {
        this.corpusPctTolerance = corpusPctTolerance;
    }

    /**
     * Gets the feeBalanceTypeCode attribute.
     * 
     * @return Returns the feeBalanceTypeCode.
     */
    public String getFeeBalanceTypeCode() {
        return feeBalanceTypeCode;
    }

    /**
     * Sets the feeBalanceTypeCode attribute value.
     * 
     * @param feeBalanceTypeCode The feeBalanceTypeCode to set.
     */
    public void setFeeBalanceTypeCode(String feeBalanceTypeCode) {
        this.feeBalanceTypeCode = feeBalanceTypeCode;
    }

    /**
     * Gets the feeByClassCode attribute.
     * 
     * @return Returns the feeByClassCode.
     */
    public boolean getFeeByClassCode() {
        return feeByClassCode;
    }

    /**
     * Sets the feeByClassCode attribute value.
     * 
     * @param feeByClassCode The feeByClassCode to set.
     */
    public void setFeeByClassCode(boolean feeByClassCode) {
        this.feeByClassCode = feeByClassCode;
    }

    /**
     * Gets the feeBySecurityCode attribute.
     * 
     * @return Returns the feeBySecurityCode.
     */
    public boolean getFeeBySecurityCode() {
        return feeBySecurityCode;
    }

    /**
     * Sets the feeBySecurityCode attribute value.
     * 
     * @param feeBySecurityCode The feeBySecurityCode to set.
     */
    public void setFeeBySecurityCode(boolean feeBySecurityCode) {
        this.feeBySecurityCode = feeBySecurityCode;
    }

    /**
     * Gets the feeByTransactionType attribute.
     * 
     * @return Returns the feeByTransactionType.
     */
    public boolean getFeeByTransactionType() {
        return feeByTransactionType;
    }

    /**
     * Sets the feeByTransactionType attribute value.
     * 
     * @param feeByTransactionType The feeByTransactionType to set.
     */
    public void setFeeByTransactionType(boolean feeByTransactionType) {
        this.feeByTransactionType = feeByTransactionType;
    }

    /**
     * Gets the feeByETranCode attribute.
     * 
     * @return Returns the feeByETranCode.
     */
    public boolean getFeeByETranCode() {
        return feeByETranCode;
    }

    /**
     * Sets the feeByETranCode attribute value.
     * 
     * @param feeByETranCode The feeByETranCode to set.
     */
    public void setFeeByETranCode(boolean feeByETranCode) {
        this.feeByETranCode = feeByETranCode;
    }

    /**
     * Gets the active attribute.
     * 
     * @return Returns the active.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute value.
     * 
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Gets the FrequencyCode.
     * 
     * @return Returns the FrequencyCode.
     */
    public FrequencyCode getFrequencyCode() {
        return frequencyCode;
    }

    /**
     * Sets the FrequencyCode.
     * 
     * @param FrequencyCode The FrequencyCode to set.
     */
    public void setFrequencyCode(FrequencyCode frequencyCode) {
        this.frequencyCode = frequencyCode;
    }

    /**
     * Gets the feeRateDefinitionCode .
     * 
     * @return Returns the feeRateDefinitionCode.
     */
    public FeeRateDefinitionCode getFeeRateDefinition() {
        return feeRateDefinition;
    }

    /**
     * Sets the developmentOfficer attribute value.
     * 
     * @param developmentOfficer The developmentOfficer to set.
     */
    public void setFeeRateDefinition(FeeRateDefinitionCode feeRateDefinition) {
        this.feeRateDefinition = feeRateDefinition;
    }

    /**
     * Gets the endowmentTransactionCode.
     * 
     * @return Returns the endowmentTransactionCode.
     */
    public EndowmentTransactionCode getEndowmentTransactionCode() {
        return endowmentTransactionCode;
    }

    /**
     * Sets the endowmentTransactionCode.
     * 
     * @param endowmentTransactionCode The endowmentTransactionCode to set.
     */
    public void setEndowmentTransactionCode(EndowmentTransactionCode endowmentTransactionCode) {
        this.endowmentTransactionCode = endowmentTransactionCode;
    }

    /**
     * Gets the feeTypeCode attribute.
     * 
     * @return Returns the feeTypeCode.
     */
    public FeeTypeCode getFeeType() {
        return feeType;
    }

    /**
     * Sets the firstCorporateContact.
     * 
     * @param firstCorporateContact The firstCorporateContact to set.
     */
    public void setFeeType(FeeTypeCode feeType) {
        this.feeType = feeType;
    }

    /**
     * Gets the feeBaseCode.
     * 
     * @return Returns the firstContactTitle.
     */
    public FeeBaseCode getFeeBase() {
        return feeBase;
    }

    /**
     * Sets the feeBaseCode.
     * 
     * @param feeBaseCode The feeBaseCode to set.
     */
    public void setFeeBase(FeeBaseCode feeBase) {
        this.feeBase = feeBase;
    }

    /**
     * Gets the feeBalanceTypeCode.
     * 
     * @return Returns the feeBalanceTypeCode.
     */
    public FeeBalanceTypeCode getFeeBalanceType() {
        return feeBalanceType;
    }

    /**
     * Sets the feeBalanceTypeCode.
     * 
     * @param feeBalanceTypeCode The feeBalanceTypeCode to set.
     */
    public void setFeeBalanceType(FeeBalanceTypeCode feeBalanceType) {
        this.feeBalanceType = feeBalanceType;
    }

    /**
     * Gets the feeClassCodes attribute.
     * 
     * @return Returns the feeClassCodes.
     */
    public List<FeeClassCode> getFeeClassCodes() {
        return feeClassCodes;
    }

    /**
     * Sets the feeClassCodes attribute value.
     * 
     * @param feeClassCodes The feeClassCodes to set.
     */
    public void setFeeClassCodes(List<FeeClassCode> feeClassCodes) {
        this.feeClassCodes = feeClassCodes;
    }

    /**
     * Gets the feeSecurity attribute.
     * 
     * @return Returns the feeSecurity.
     */
    public List<FeeSecurity> getFeeSecurity() {
        return feeSecurity;
    }

    /**
     * Sets the feeSecurity attribute value.
     * 
     * @param feeSecurity The feeSecurity to set.
     */
    public void setFeeSecurity(List<FeeSecurity> feeSecurity) {
        this.feeSecurity = feeSecurity;
    }

    /**
     * Gets the feePaymentTypes attribute.
     * 
     * @return Returns the feePaymentTypes.
     */
    public List<FeePaymentType> getFeePaymentTypes() {
        return feePaymentTypes;
    }

    /**
     * Sets the feePaymentTypes attribute value.
     * 
     * @param feePaymentTypes The feePaymentTypes to set.
     */
    public void setFeePaymentTypes(List<FeePaymentType> feePaymentTypes) {
        this.feePaymentTypes = feePaymentTypes;
    }

    /**
     * Gets the feeTransactions attribute.
     * 
     * @return Returns the feeTransactions.
     */
    public List<FeeTransaction> getFeeTransactions() {
        return feeTransactions;
    }

    /**
     * Sets the feeTransactions attribute value.
     * 
     * @param feeTransactions The feeTransactions to set.
     */
    public void setFeeTransactions(List<FeeTransaction> feeTransactions) {
        this.feeTransactions = feeTransactions;
    }

    /**
     * Gets the feeEndowmentTransactionCodes attribute.
     * 
     * @return Returns the feeEndowmentTransactionCodes.
     */
    public List<FeeEndowmentTransactionCode> getFeeEndowmentTransactionCodes() {
        return feeEndowmentTransactionCodes;
    }

    /**
     * Sets the feeEndowmentTransactionCodes attribute value.
     * 
     * @param feeEndowmentTransactionCodes The feeEndowmentTransactionCodes to set.
     */
    public void setFeeEndowmentTransactionCodes(List<FeeEndowmentTransactionCode> feeEndowmentTransactionCodes) {
        this.feeEndowmentTransactionCodes = feeEndowmentTransactionCodes;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
        m.put(EndowPropertyConstants.FEE_METHOD, this.code);
        return m;
    }

    /**
     * gets the codeAndDescription. returns codeAndDescription.
     */
    public String getCodeAndDescription() {
        if (StringUtils.isEmpty(this.code)) {
            return KFSConstants.EMPTY_STRING;
        }
        return getCode() + "-" + getName();
    }
    /**
     * gets the minimumFeeThreshold. returns minimumFeeThreshold.
     */    
    public KualiDecimal getMinimumFeeThreshold() {
        if (minimumFeeThreshold == null) {
            return new KualiDecimal("0");
        }
        return minimumFeeThreshold;
    }

    /**
     * Sets the minimumFeeThreshold attribute value.
     * @param minimumFeeThreshold The minimumFeeThreshold to set.
     */
    public void setMinimumFeeThreshold(KualiDecimal minimumFeeThreshold) {
        this.minimumFeeThreshold = minimumFeeThreshold;
    }
}
