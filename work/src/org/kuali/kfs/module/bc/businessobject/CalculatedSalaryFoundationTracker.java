/*
 * Copyright 2006 The Kuali Foundation
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

package org.kuali.kfs.module.bc.businessobject;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.LinkedHashMap;

import org.apache.cxf.common.util.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.businessobject.SubObjectCode;
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.FinancialSystemUserService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 *
 */
public class CalculatedSalaryFoundationTracker extends PersistableBusinessObjectBase {

    private Integer universityFiscalYear;
    private String chartOfAccountsCode;
    private String accountNumber;
    private String subAccountNumber;
    private String financialObjectCode;
    private String financialSubObjectCode;
    private String positionNumber;
    private String emplid;
    private String name;
    private Timestamp csfCreateTimestamp;
    private String csfDeleteCode;
    private KualiDecimal csfAmount;
    private BigDecimal csfFullTimeEmploymentQuantity;
    private BigDecimal csfTimePercent;
    private String csfFundingStatusCode;
    private Integer employeeRecord;
    private String earnCode;
    private Integer additionalSequence;
    private Date effectiveDate;
    private Integer effectiveSequence;

    private ObjectCode financialObject;
    private Chart chartOfAccounts;
    private Account account;
    private SubAccount subAccount;
    private SubObjectCode financialSubObject;
    private CalculatedSalaryFoundationTrackerOverride calculatedSalaryFoundationTrackerOverride;

    private transient SystemOptions universityFiscal;
    private final int PERCENTAGE_SCALE = 2;

    /**
     * Default constructor.
     */
    public CalculatedSalaryFoundationTracker() {

    }

    /**
     * Gets the universityFiscalYear attribute.
     *
     * @return Returns the universityFiscalYear
     */
    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    /**
     * Sets the universityFiscalYear attribute.
     *
     * @param universityFiscalYear The universityFiscalYear to set.
     */
    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }


    /**
     * Gets the chartOfAccountsCode attribute.
     *
     * @return Returns the chartOfAccountsCode
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode attribute.
     *
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }


    /**
     * Gets the accountNumber attribute.
     *
     * @return Returns the accountNumber
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Sets the accountNumber attribute.
     *
     * @param accountNumber The accountNumber to set.
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }


    /**
     * Gets the subAccountNumber attribute.
     *
     * @return Returns the subAccountNumber
     */
    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    /**
     * Sets the subAccountNumber attribute.
     *
     * @param subAccountNumber The subAccountNumber to set.
     */
    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }


    /**
     * Gets the financialObjectCode attribute.
     *
     * @return Returns the financialObjectCode
     */
    public String getFinancialObjectCode() {
        return financialObjectCode;
    }

    /**
     * Sets the financialObjectCode attribute.
     *
     * @param financialObjectCode The financialObjectCode to set.
     */
    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }


    /**
     * Gets the financialSubObjectCode attribute.
     *
     * @return Returns the financialSubObjectCode
     */
    public String getFinancialSubObjectCode() {
        return financialSubObjectCode;
    }

    /**
     * Sets the financialSubObjectCode attribute.
     *
     * @param financialSubObjectCode The financialSubObjectCode to set.
     */
    public void setFinancialSubObjectCode(String financialSubObjectCode) {
        this.financialSubObjectCode = financialSubObjectCode;
    }


    /**
     * Gets the positionNumber attribute.
     *
     * @return Returns the positionNumber
     */
    public String getPositionNumber() {
        return positionNumber;
    }

    /**
     * Sets the positionNumber attribute.
     *
     * @param positionNumber The positionNumber to set.
     */
    public void setPositionNumber(String positionNumber) {
        this.positionNumber = positionNumber;
    }


    /**
     * Gets the emplid attribute.
     *
     * @return Returns the emplid
     */
    public String getEmplid() {
        return emplid;
    }

    /**
     * Sets the emplid attribute.
     *
     * @param emplid The emplid to set.
     */
    public void setEmplid(String emplid) {
        this.emplid = emplid;
    }


    /**
     * Gets the csfCreateTimestamp attribute.
     *
     * @return Returns the csfCreateTimestamp
     */
    public Timestamp getCsfCreateTimestamp() {
        return csfCreateTimestamp;
    }

    /**
     * Sets the csfCreateTimestamp attribute.
     *
     * @param csfCreateTimestamp The csfCreateTimestamp to set.
     */
    public void setCsfCreateTimestamp(Timestamp csfCreateTimestamp) {
        this.csfCreateTimestamp = csfCreateTimestamp;
    }


    /**
     * Gets the csfDeleteCode attribute.
     *
     * @return Returns the csfDeleteCode
     */
    public String getCsfDeleteCode() {
        return csfDeleteCode;
    }

    /**
     * Sets the csfDeleteCode attribute.
     *
     * @param csfDeleteCode The csfDeleteCode to set.
     */
    public void setCsfDeleteCode(String csfDeleteCode) {
        this.csfDeleteCode = csfDeleteCode;
    }


    /**
     * Gets the csfAmount attribute.
     *
     * @return Returns the csfAmount
     */
    public KualiDecimal getCsfAmount() {
        return csfAmount;
    }

    /**
     * Sets the csfAmount attribute.
     *
     * @param csfAmount The csfAmount to set.
     */
    public void setCsfAmount(KualiDecimal csfAmount) {
        this.csfAmount = csfAmount;
    }


    /**
     * Gets the csfFullTimeEmploymentQuantity attribute.
     *
     * @return Returns the csfFullTimeEmploymentQuantity
     */
    public BigDecimal getCsfFullTimeEmploymentQuantity() {
        return csfFullTimeEmploymentQuantity;
    }

    /**
     * Sets the csfFullTimeEmploymentQuantity attribute.
     *
     * @param csfFullTimeEmploymentQuantity The csfFullTimeEmploymentQuantity to set.
     */
    public void setCsfFullTimeEmploymentQuantity(BigDecimal csfFullTimeEmploymentQuantity) {
        this.csfFullTimeEmploymentQuantity = csfFullTimeEmploymentQuantity;
    }


    /**
     * Gets the csfTimePercent attribute. Returns 2 decimal places, reguardless.
     *
     * @return Returns the csfTimePercent
     */
    public BigDecimal getCsfTimePercent() {

        BigDecimal bigDecValue = this.csfTimePercent;
        bigDecValue = bigDecValue.setScale(PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP);
        return bigDecValue;
    }

    /**
     * Sets the csfTimePercent attribute.
     *
     * @param csfTimePercent The csfTimePercent to set.
     */
    public void setCsfTimePercent(BigDecimal csfTimePercent) {
        this.csfTimePercent = csfTimePercent;
    }


    /**
     * Gets the csfFundingStatusCode attribute.
     *
     * @return Returns the csfFundingStatusCode
     */
    public String getCsfFundingStatusCode() {
        return csfFundingStatusCode;
    }

    /**
     * Sets the csfFundingStatusCode attribute.
     *
     * @param csfFundingStatusCode The csfFundingStatusCode to set.
     */
    public void setCsfFundingStatusCode(String csfFundingStatusCode) {
        this.csfFundingStatusCode = csfFundingStatusCode;
    }


    /**
     * Gets the employeeRecord attribute.
     *
     * @return Returns the employeeRecord
     */
    public Integer getEmployeeRecord() {
        return employeeRecord;
    }

    /**
     * Sets the employeeRecord attribute.
     *
     * @param employeeRecord The employeeRecord to set.
     */
    public void setEmployeeRecord(Integer employeeRecord) {
        this.employeeRecord = employeeRecord;
    }


    /**
     * Gets the earnCode attribute.
     *
     * @return Returns the earnCode
     */
    public String getEarnCode() {
        return earnCode;
    }

    /**
     * Sets the earnCode attribute.
     *
     * @param earnCode The earnCode to set.
     */
    public void setEarnCode(String earnCode) {
        this.earnCode = earnCode;
    }


    /**
     * Gets the additionalSequence attribute.
     *
     * @return Returns the additionalSequence
     */
    public Integer getAdditionalSequence() {
        return additionalSequence;
    }

    /**
     * Sets the additionalSequence attribute.
     *
     * @param additionalSequence The additionalSequence to set.
     */
    public void setAdditionalSequence(Integer additionalSequence) {
        this.additionalSequence = additionalSequence;
    }


    /**
     * Gets the effectiveDate attribute.
     *
     * @return Returns the effectiveDate
     */
    public Date getEffectiveDate() {
        return effectiveDate;
    }

    /**
     * Sets the effectiveDate attribute.
     *
     * @param effectiveDate The effectiveDate to set.
     */
    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }


    /**
     * Gets the effectiveSequence attribute.
     *
     * @return Returns the effectiveSequence
     */
    public Integer getEffectiveSequence() {
        return effectiveSequence;
    }

    /**
     * Sets the effectiveSequence attribute.
     *
     * @param effectiveSequence The effectiveSequence to set.
     */
    public void setEffectiveSequence(Integer effectiveSequence) {
        this.effectiveSequence = effectiveSequence;
    }


    /**
     * Gets the financialObject attribute.
     *
     * @return Returns the financialObject
     */
    public ObjectCode getFinancialObject() {
        return financialObject;
    }

    /**
     * Sets the financialObject attribute.
     *
     * @param financialObject The financialObject to set.
     * @deprecated
     */
    @Deprecated
    public void setFinancialObject(ObjectCode financialObject) {
        this.financialObject = financialObject;
    }

    /**
     * Gets the chartOfAccounts attribute.
     *
     * @return Returns the chartOfAccounts
     */
    public Chart getChartOfAccounts() {
        return chartOfAccounts;
    }

    /**
     * Sets the chartOfAccounts attribute.
     *
     * @param chartOfAccounts The chartOfAccounts to set.
     * @deprecated
     */
    @Deprecated
    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    /**
     * Gets the account attribute.
     *
     * @return Returns the account
     */
    public Account getAccount() {
        return account;
    }

    /**
     * Sets the account attribute.
     *
     * @param account The account to set.
     * @deprecated
     */
    @Deprecated
    public void setAccount(Account account) {
        this.account = account;
    }

    /**
     * @return Returns the subAccount.
     */
    public SubAccount getSubAccount() {
        return subAccount;
    }

    /**
     * @param subAccount The subAccount to set.
     * @deprecated
     */
    @Deprecated
    public void setSubAccount(SubAccount subAccount) {
        this.subAccount = subAccount;
    }


    /**
     * Gets the universityFiscal attribute.
     *
     * @return Returns the universityFiscal.
     */
    public SystemOptions getUniversityFiscal() {
        return universityFiscal;
    }

    /**
     * Sets the universityFiscal attribute value.
     *
     * @param universityFiscal The universityFiscal to set.
     */
    public void setUniversityFiscal(SystemOptions universityFiscal) {
        this.universityFiscal = universityFiscal;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        if (this.universityFiscalYear != null) {
            m.put("universityFiscalYear", this.universityFiscalYear.toString());
        }
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("accountNumber", this.accountNumber);
        m.put("subAccountNumber", this.subAccountNumber);
        m.put("financialObjectCode", this.financialObjectCode);
        m.put("financialSubObjectCode", this.financialSubObjectCode);
        m.put("positionNumber", this.positionNumber);
        m.put("emplid", this.emplid);
        if (this.csfCreateTimestamp != null) {
            m.put("csfCreateTimestamp", this.csfCreateTimestamp.toString());
        }
        return m;
    }

    public String getName() {
        String name = SpringContext.getBean(FinancialSystemUserService.class).getPersonNameByEmployeeId(getEmplid());
        if (!StringUtils.isEmpty(name)) {
            return name;
        }
        return LaborConstants.BalanceInquiries.UnknownPersonName;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the financialSubObject attribute.
     *
     * @return Returns the financialSubObject.
     */
    public SubObjectCode getFinancialSubObject() {
        return financialSubObject;
    }

    /**
     * Sets the financialSubObject attribute value.
     *
     * @param financialSubObject The financialSubObject to set.
     * @deprecated
     */
    @Deprecated
    public void setFinancialSubObject(SubObjectCode financialSubObject) {
        this.financialSubObject = financialSubObject;
    }

    public CalculatedSalaryFoundationTrackerOverride getCalculatedSalaryFoundationTrackerOverride() {
        return calculatedSalaryFoundationTrackerOverride;
    }

    public void setCalculatedSalaryFoundationTrackerOverride(CalculatedSalaryFoundationTrackerOverride calculatedSalaryFoundationTrackerOverride) {
        this.calculatedSalaryFoundationTrackerOverride = calculatedSalaryFoundationTrackerOverride;
    }

    public boolean isOverride() {

        return (getCalculatedSalaryFoundationTrackerOverride() != null);
    }

    public String getPositionLookupPositionNumber() {
        if ( isOverride() ) {
            return getCalculatedSalaryFoundationTrackerOverride().getPositionNumber();
        }

        return getPositionNumber();
    }

    public KualiDecimal getPositionLookupCsfAmount() {
        if ( isOverride() ) {
            return getCalculatedSalaryFoundationTrackerOverride().getCsfAmount();
        }

        return getCsfAmount();
    }

    public BigDecimal getPositionLookupCsfFullTimeEmploymentQuantity() {
        if ( isOverride() ) {
            return getCalculatedSalaryFoundationTrackerOverride().getCsfFullTimeEmploymentQuantity();
        }

        return getCsfFullTimeEmploymentQuantity();
    }

    public String getPositionLookupOverrideFlag() {

        return isOverride() ? "Y" : "N";
    }
}

