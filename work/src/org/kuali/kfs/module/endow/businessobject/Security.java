/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.businessobject;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.module.endow.document.service.TicklerService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Business Object for Security table.
 */
public class Security extends PersistableBusinessObjectBase implements MutableInactivatable {

    private String id;
    private String description;
    private String tickerSymbol;
    private String securityClassCode;
    private String securitySubclassCode;
    private Date maturityDate;
    private BigDecimal unitValue;
    private BigDecimal unitsHeld;
    private Date valuationDate;
    private String unitValueSource;
    private BigDecimal previousUnitValue;
    private Date previousUnitValueDate;
    private BigDecimal carryValue;
    private Date lastTransactionDate;
    private String incomePayFrequency;
    private Date incomeNextPayDate;
    private BigDecimal incomeRate;
    private Date incomeChangeDate;
    private Date issueDate;
    private Date dividendRecordDate;
    private Date exDividendDate;
    private Date dividendPayDate;
    private BigDecimal dividendAmount;
    private BigDecimal commitmentAmount;
    private BigDecimal nextFiscalYearDistributionAmount;
    private BigDecimal securityValueByMarket;
    private boolean active;

    // this field is not saved in the db,
    // it is used so that the user can enter the first 8 chars of the security ID while the ninth
    // digit is computed using the mod10 method
    private String userEnteredSecurityIDprefix;

    // this field is not saved in the database but computed based on Sum of the HLDG _MVAL for all records for the Security in
    // END_CURR_TAX_LOT_BAL_T
    private BigDecimal marketValue;

    private ClassCode classCode;
    private FrequencyCode frequencyCode;
    private SubclassCode subclassCode;

    List<Tickler> ticklers;

    /**
     * Constructs a Security.java.
     */
    public Security() {
        super();
        unitValue = BigDecimal.ONE;
        unitsHeld = BigDecimal.ZERO;
        carryValue = BigDecimal.ZERO;
        ticklers = new ArrayList<Tickler>();
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(EndowPropertyConstants.SECURITY_ID, this.id);
        return m;
    }

    /**
     * @see org.kuali.rice.core.api.mo.common.active.MutableInactivatable#isActive()
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @see org.kuali.rice.core.api.mo.common.active.MutableInactivatable#setActive(boolean)
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * This method gets carryValue
     * 
     * @return carryValue
     */
    public BigDecimal getCarryValue() {
        return carryValue;
    }

    /**
     * This method sets carryValue.
     * 
     * @param carryValue
     */
    public void setCarryValue(BigDecimal carryValue) {
        this.carryValue = carryValue;
    }


    /**
     * This method gets the commitmentAmount.
     * 
     * @return commitmentAmount
     */
    public BigDecimal getCommitmentAmount() {
        return commitmentAmount;
    }

    /**
     * This method sets the commitmentAmount.
     * 
     * @param commitmentAmount
     */
    public void setCommitmentAmount(BigDecimal commitmentAmount) {
        this.commitmentAmount = commitmentAmount;
    }

    /**
     * This method gets the description.
     * 
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * This method sets the description.
     * 
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * This method gets the dividendAmount.
     * 
     * @return dividendAmount
     */
    public BigDecimal getDividendAmount() {
        if (dividendAmount == null) {
            return BigDecimal.ZERO;
        }
        
        return dividendAmount;
    }

    /**
     * This method sets the dividendAmount.
     * 
     * @param dividendAmount
     */
    public void setDividendAmount(BigDecimal dividendAmount) {
        this.dividendAmount = dividendAmount;
    }

    /**
     * This method gets the dividendPayDate
     * 
     * @return dividendPayDate
     */
    public Date getDividendPayDate() {
        return dividendPayDate;
    }

    /**
     * This method sets the dividendPayDate.
     * 
     * @param dividendPayDate
     */
    public void setDividendPayDate(Date dividendPayDate) {
        this.dividendPayDate = dividendPayDate;
    }

    /**
     * This method gets the dividendRecordDate.
     * 
     * @return dividendRecordDate
     */
    public Date getDividendRecordDate() {
        return dividendRecordDate;
    }

    /**
     * This method sets the dividendRecordDate.
     * 
     * @param dividendRecordDate
     */
    public void setDividendRecordDate(Date dividendRecordDate) {
        this.dividendRecordDate = dividendRecordDate;
    }

    /**
     * This method gets the exDividendDate.
     * 
     * @return exDividendDate
     */
    public Date getExDividendDate() {
        return exDividendDate;
    }

    /**
     * This method sets the exDividendDate.
     * 
     * @param exDividendDate
     */
    public void setExDividendDate(Date exDividendDate) {
        this.exDividendDate = exDividendDate;
    }

    /**
     * This method gets the id.
     * 
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * This method sets the id.
     * 
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * This method gets the incomeChangeDate.
     * 
     * @return incomeChangeDate
     */
    public Date getIncomeChangeDate() {
        return incomeChangeDate;
    }

    /**
     * This method sets the incomeChangeDate.
     * 
     * @param incomeChangeDate
     */
    public void setIncomeChangeDate(Date incomeChangeDate) {
        this.incomeChangeDate = incomeChangeDate;
    }

    /**
     * This method gets the incomeNextPayDate
     * 
     * @return incomeNextPayDate
     */
    public Date getIncomeNextPayDate() {
        return incomeNextPayDate;
    }

    /**
     * This method sets the incomeNextPayDate
     * 
     * @param incomeNextPayDate
     */
    public void setIncomeNextPayDate(Date incomeNextPayDate) {
        this.incomeNextPayDate = incomeNextPayDate;
    }

    /**
     * This method gets the incomePayFrequency.
     * 
     * @return incomePayFrequency
     */
    public String getIncomePayFrequency() {
        return incomePayFrequency;
    }

    /**
     * This method sets the incomePayFrequency
     * 
     * @param incomePayFrequency
     */
    public void setIncomePayFrequency(String incomePayFrequency) {
        this.incomePayFrequency = incomePayFrequency;
    }

    /**
     * This method gets the incomeRate
     * 
     * @return incomeRate
     */
    public BigDecimal getIncomeRate() {
        return incomeRate;
    }

    /**
     * This method sets the incomeRate
     * 
     * @param incomeRate
     */
    public void setIncomeRate(BigDecimal incomeRate) {
        this.incomeRate = incomeRate;
    }

    /**
     * This method gets the incomeRate.
     * 
     * @return incomeRate
     */
    public Date getIssueDate() {
        return issueDate;
    }

    /**
     * This method sets the incomeRate.
     * 
     * @param issueDate
     */
    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    /**
     * This method gets the lastTransactionDate.
     * 
     * @return lastTransactionDate
     */
    public Date getLastTransactionDate() {
        return lastTransactionDate;
    }

    /**
     * This method sets the lastTransactionDate.
     * 
     * @param lastTransactionDate
     */
    public void setLastTransactionDate(Date lastTransactionDate) {
        this.lastTransactionDate = lastTransactionDate;
    }

    /**
     * This method gets the maturityDate
     * 
     * @return maturityDate
     */
    public Date getMaturityDate() {
        return maturityDate;
    }

    /**
     * This method sets the maturityDate
     * 
     * @param maturityDate
     */
    public void setMaturityDate(Date maturityDate) {
        this.maturityDate = maturityDate;
    }

    /**
     * This method gets the securitySubclassCode
     * 
     * @return securitySubclassCode
     */
    public String getSecuritySubclassCode() {
        return securitySubclassCode;
    }

    /**
     * This method sets the securitySubclassCode
     * 
     * @param subclassCode
     */
    public void setSecuritySubclassCode(String subclassCode) {
        this.securitySubclassCode = subclassCode;
    }

    /**
     * This method gets the tickerSymbol.
     * 
     * @return the tickerSymbol
     */
    public String getTickerSymbol() {
        return tickerSymbol;
    }

    /**
     * This method sets the tickerSymbol.
     * 
     * @param tickerSymbol
     */
    public void setTickerSymbol(String tickerSymbol) {
        this.tickerSymbol = tickerSymbol;
    }

    /**
     * This method gets the unitsHeld.
     * 
     * @return unitsHeld
     */
    public BigDecimal getUnitsHeld() {
        return unitsHeld;
    }

    /**
     * This method sets the unitsHeld.
     * 
     * @param unitsHeld
     */
    public void setUnitsHeld(BigDecimal unitsHeld) {
        this.unitsHeld = unitsHeld;
    }

    /**
     * This method gets the unitValue.
     * 
     * @return unitValue
     */
    public BigDecimal getUnitValue() {
        return unitValue;
    }

    /**
     * This method sets the unitValue.
     * 
     * @param unitValue
     */
    public void setUnitValue(BigDecimal unitValue) {

        if (unitValue != null) {
            this.unitValue = unitValue.setScale(EndowConstants.Scale.SECURITY_UNIT_VALUE, BigDecimal.ROUND_HALF_UP);
        }
        else {
            this.unitValue = unitValue;
        }
    }

    /**
     * This method gets the valuationDate.
     * 
     * @return valuationDate
     */
    public Date getValuationDate() {
        return valuationDate;
    }

    /**
     * This method sets the valuationDate.
     * 
     * @param valuationDate
     */
    public void setValuationDate(Date valuationDate) {
        this.valuationDate = valuationDate;
    }

    /**
     * This method gets the previousUnitValue.
     * 
     * @return previousUnitValue
     */
    public BigDecimal getPreviousUnitValue() {
        return previousUnitValue;
    }

    /**
     * This method sets the previousUnitValue.
     * 
     * @param previousUnitValue
     */
    public void setPreviousUnitValue(BigDecimal previousUnitValue) {
        this.previousUnitValue = previousUnitValue;
    }

    /**
     * This method gets the previousUnitValueDate.
     * 
     * @return previousUnitValueDate
     */
    public Date getPreviousUnitValueDate() {
        return previousUnitValueDate;
    }

    /**
     * This method sets previousUnitValueDate.
     * 
     * @param previousUnitValueDate
     */
    public void setPreviousUnitValueDate(Date previousUnitValueDate) {
        this.previousUnitValueDate = previousUnitValueDate;
    }

    /**
     * This method gets the unitValueSource.
     * 
     * @return unitValueSource
     */
    public String getUnitValueSource() {
        return unitValueSource;
    }

    /**
     * This method sets the unitValueSource.
     * 
     * @param unitValueSource
     */
    public void setUnitValueSource(String unitValueSource) {
        this.unitValueSource = unitValueSource;
    }

    /**
     * This method gets the classCode.
     * 
     * @return classCode
     */
    public ClassCode getClassCode() {
        return classCode;
    }

    /**
     * This method sets the classCode.
     * 
     * @param classCode
     */
    public void setClassCode(ClassCode classCode) {
        this.classCode = classCode;
    }

    /**
     * This method gets the securityClassCode
     * 
     * @return securityClassCode
     */
    public String getSecurityClassCode() {
        return securityClassCode;
    }

    /**
     * This method sets the securityClassCode.
     * 
     * @param securityClassCode
     */
    public void setSecurityClassCode(String securityClassCode) {
        this.securityClassCode = securityClassCode;
    }

    /**
     * This method returns the frequencyCode.
     * 
     * @return frequencyCode
     */
    public FrequencyCode getFrequencyCode() {
        return frequencyCode;
    }

    /**
     * This method sets the frequencyCode.
     * 
     * @param frequencyCode
     */
    public void setFrequencyCode(FrequencyCode frequencyCode) {
        this.frequencyCode = frequencyCode;
    }

    /**
     * This method gets the subclassCode.
     * 
     * @return subclassCode
     */
    public SubclassCode getSubclassCode() {
        return subclassCode;
    }

    /**
     * This method sets the subclassCode.
     * 
     * @param subclassCode
     */
    public void setSubclassCode(SubclassCode subclassCode) {
        this.subclassCode = subclassCode;
    }

    /**
     * This method gets the user entered security ID prefix (first 8 characters).
     * 
     * @return
     */
    public String getUserEnteredSecurityIDprefix() {
        return userEnteredSecurityIDprefix;
    }

    /**
     * This method sets the user entered security ID prefix.
     * 
     * @param userEnteredSecurityIDprefix
     */
    public void setUserEnteredSecurityIDprefix(String userEnteredSecurityIDprefix) {
        this.userEnteredSecurityIDprefix = userEnteredSecurityIDprefix;
    }

    /**
     * This method gets the market value of the security.
     * 
     * @return marketValue
     */
    public BigDecimal getMarketValue() {
        return marketValue;
    }

    /**
     * This method sete the marketValue for the security.
     * 
     * @param marketValue
     */
    public void setMarketValue(BigDecimal marketValue) {

        if (marketValue != null) {
            this.marketValue = marketValue.setScale(EndowConstants.Scale.SECURITY_MARKET_VALUE, BigDecimal.ROUND_HALF_UP);
        }
        else {
            this.marketValue = marketValue;
        }
    }

    /**
     * @see org.kuali.rice.krad.bo.PersistableBusinessObjectBase#afterLookup(org.apache.ojb.broker.PersistenceBroker)
     */
    @Override
    protected void postLoad() {
        super.postLoad();

        KEMService kemService = SpringContext.getBean(KEMService.class);

        // after a lookup is performed for Securities the market values is computed for display and the UserEnteredSecurityIDprefix
        // is retrieved and set so that we don't get a required field error
        String securityID = this.getId();
        BigDecimal marketValue = kemService.getMarketValue(this.id);

        this.setMarketValue(marketValue);
        // set user entered security ID prefix based on the security ID so that we don't get a required field error.
        this.setUserEnteredSecurityIDprefix(securityID.substring(0, securityID.length() - 1));

    }

    /**
     * Gets the nextFiscalYearDistributionAmount.
     * 
     * @return nextFiscalYearDistributionAmount
     */
    public BigDecimal getNextFiscalYearDistributionAmount() {
        return nextFiscalYearDistributionAmount;
    }

    /**
     * Sets the nextFiscalYearDistributionAmount.
     * 
     * @param nextFiscalYearDistributionAmount
     */
    public void setNextFiscalYearDistributionAmount(BigDecimal nextFiscalYearDistributionAmount) {
        this.nextFiscalYearDistributionAmount = nextFiscalYearDistributionAmount;
    }

    /**
     * Gets the CurrentHolders link text
     * 
     * @return the text to be displayed for the current holders link.
     */
    public String getCurrentHolders() {
        return EndowConstants.LOOKUP_LINK;
    }

    /**
     * Gets the HoldersInHistory link text
     * 
     * @return the text to be displayed for the holders in history link.
     */
    public String getHoldersInHistory() {
        return EndowConstants.LOOKUP_LINK;
    }

    public String getSecurityIdDescription() {
        String securityID = getId() != null ? getId() : "";
        String description = getDescription() != null ? getDescription() : "";
        String securityIdLabel = securityID + " - " + description;
        return securityIdLabel;
    }

    /**
     * Gets the codeAndDescription text
     * 
     * @return the code and description in the form: xx-xxxxxxxxxx
     */
    public String getCodeAndDescription() {
        if (StringUtils.isEmpty(this.getId())) {
            return KFSConstants.EMPTY_STRING;
        }
        return this.getId() + "-" + this.getDescription();
    }

    /**
     * Gets the active ticklers for this Security.
     * 
     * @return ticklers
     */
    public List<Tickler> getTicklers() {
        return SpringContext.getBean(TicklerService.class).getSecurityActiveTicklers(this.id);
    }

    /**
     * Sets the ticklers.
     * 
     * @param ticklers
     */
    public void setTicklers(List<Tickler> ticklers) {
        this.ticklers = ticklers;
    }

    /**
     * Gets the securityValueByMarket.
     * 
     * @return securityValueByMarket
     */
    public BigDecimal getSecurityValueByMarket() {
        return securityValueByMarket;
    }

    /**
     * Sets the securityValueByMarket.
     * 
     * @param securityValueByMarket
     */
    public void setSecurityValueByMarket(BigDecimal securityValueByMarket) {
        this.securityValueByMarket = securityValueByMarket;
    }

}
