/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.module.chart.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.service.impl.PersistenceStructureServiceImpl;
import org.kuali.kfs.bo.Options;

/**
 * 
 */
public class SubObjCd extends PersistableBusinessObjectBase {

    private static final long serialVersionUID = -5292158248714650271L;

    static {
        PersistenceStructureServiceImpl.referenceConversionMap.put(SubObjCd.class, SubObjCdCurrent.class);
    }
    
    /**
     * Default no-arg constructor.
     */
    public SubObjCd() {

    }
    /**
     * 
     * Constructs an active SubObjCd.java with the given primary key.
     * 
     * @param universityFiscalYear
     * @param chartOfAccountsCode
     * @param accountNumber
     * @param financialObjectCode
     * @param financialSubObjectCode
     */
    public SubObjCd(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String financialObjectCode, String financialSubObjectCode) {
        this.universityFiscalYear = universityFiscalYear;
        this.chartOfAccountsCode = chartOfAccountsCode;
        this.accountNumber = accountNumber;
        this.financialObjectCode = financialObjectCode;
        this.financialSubObjectCode = financialSubObjectCode;
        this.financialSubObjectActiveIndicator = true;
    }

    private String chartOfAccountsCode;
    private String accountNumber;
    private String financialObjectCode;
    private String financialSubObjectCode;
    private String financialSubObjectCodeName;
    private String financialSubObjectCdshortNm;
    private boolean financialSubObjectActiveIndicator;
    private Integer universityFiscalYear;

    private Chart chartOfAccounts;
    private Account account;
    private ObjectCode financialObject;
    private Options universityFiscal;

    public String getFinancialSubObjectCode() {
        return financialSubObjectCode;
    }

    public void setFinancialSubObjectCode(String financialSubObjectCode) {
        this.financialSubObjectCode = financialSubObjectCode;
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


    public ObjectCode getFinancialObject() {
        return financialObject;
    }

    /**
     * 
     * @deprecated
     */
    public void setFinancialObject(ObjectCode financialObject) {
        this.financialObject = financialObject;
    }

    /**
     * Gets the financialSubObjectCodeName attribute.
     * 
     * @return Returns the financialSubObjectCodeName
     * 
     */
    public String getFinancialSubObjectCodeName() {
        return financialSubObjectCodeName;
    }

    /**
     * Sets the financialSubObjectCodeName attribute.
     * 
     * @param financialSubObjectCodeName The financialSubObjectCodeName to set.
     * 
     */
    public void setFinancialSubObjectCodeName(String financialSubObjectCodeName) {
        this.financialSubObjectCodeName = financialSubObjectCodeName;
    }

    /**
     * Gets the financialSubObjectCdshortNm attribute.
     * 
     * @return Returns the financialSubObjectCdshortNm
     * 
     */
    public String getFinancialSubObjectCdshortNm() {
        return financialSubObjectCdshortNm;
    }

    /**
     * Sets the financialSubObjectCdshortNm attribute.
     * 
     * @param financialSubObjectCdshortNm The financialSubObjectCdshortNm to set.
     * 
     */
    public void setFinancialSubObjectCdshortNm(String financialSubObjectCdshortNm) {
        this.financialSubObjectCdshortNm = financialSubObjectCdshortNm;
    }

    /**
     * Gets the financialSubObjectActiveIndicator attribute.
     * 
     * @return Returns the financialSubObjectActiveIndicator
     * 
     */
    public boolean isFinancialSubObjectActiveIndicator() {
        return financialSubObjectActiveIndicator;
    }

    /**
     * Sets the financialSubObjectActiveIndicator attribute.
     * 
     * @param financialSubObjectActiveIndicator The financialSubObjectActiveIndicator to set.
     * 
     */
    public void setFinancialSubObjectActiveIndicator(boolean financialSubObjectActiveIndicator) {
        this.financialSubObjectActiveIndicator = financialSubObjectActiveIndicator;
    }

    /**
     * Gets the universityFiscal attribute.
     * 
     * @return Returns the universityFiscal
     * 
     */
    public Options getUniversityFiscal() {
        return universityFiscal;
    }

    /**
     * Sets the universityFiscal attribute.
     * 
     * @param universityFiscal The universityFiscal to set.
     * @deprecated
     */
    public void setUniversityFiscal(Options universityFiscal) {
        this.universityFiscal = universityFiscal;
    }

    /**
     * Gets the chartOfAccounts attribute.
     * 
     * @return Returns the chartOfAccounts
     * 
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
    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    /**
     * Gets the account attribute.
     * 
     * @return Returns the account
     * 
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
    public void setAccount(Account account) {
        this.account = account;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();

        m.put("universityFiscalYear", this.universityFiscalYear);
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("accountNumber", this.accountNumber);
        m.put("financialObjectCode", this.financialObjectCode);
        m.put("financialSubObjectCode", this.financialSubObjectCode);

        return m;
    }

}
