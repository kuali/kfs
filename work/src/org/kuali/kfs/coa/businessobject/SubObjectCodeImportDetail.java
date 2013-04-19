/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.kfs.coa.businessobject;

import java.util.LinkedHashMap;

import org.apache.log4j.Logger;
import org.kuali.kfs.sys.businessobject.MassImportLineBase;
import org.kuali.kfs.sys.businessobject.SystemOptions;

/**
 * This class is the detail business object for Sub Account Import Global Maintenance Document
 */
public class SubObjectCodeImportDetail extends MassImportLineBase {

    private static final Logger LOG = Logger.getLogger(SubObjectCodeImportDetail.class);
    private String documentNumber;
    private Integer universityFiscalYear;
    private String chartOfAccountsCode;
    private String accountNumber;
    private String financialObjectCode;
    private String financialSubObjectCode;
    private String financialSubObjectCodeName;
    private String financialSubObjectCdshortNm;
    private boolean active;

    private Chart chartOfAccounts;
    private Account account;
    private ObjectCode financialObject;
    private SystemOptions universityFiscal;

    /**
     * Default constructor.
     */
    public SubObjectCodeImportDetail() {

    }


    /**
     * Gets the documentNumber attribute.
     * @return Returns the documentNumber.
     */
    public String getDocumentNumber() {
        return documentNumber;
    }


    /**
     * Sets the documentNumber attribute value.
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }


    /**
     * Gets the chartOfAccountsCode attribute.
     * @return Returns the chartOfAccountsCode.
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }


    /**
     * Sets the chartOfAccountsCode attribute value.
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }


    /**
     * Gets the accountNumber attribute.
     * @return Returns the accountNumber.
     */
    public String getAccountNumber() {
        return accountNumber;
    }


    /**
     * Sets the accountNumber attribute value.
     * @param accountNumber The accountNumber to set.
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }


    /**
     * Gets the financialObjectCode attribute.
     * @return Returns the financialObjectCode.
     */
    public String getFinancialObjectCode() {
        return financialObjectCode;
    }


    /**
     * Sets the financialObjectCode attribute value.
     * @param financialObjectCode The financialObjectCode to set.
     */
    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }


    /**
     * Gets the financialSubObjectCode attribute.
     * @return Returns the financialSubObjectCode.
     */
    public String getFinancialSubObjectCode() {
        return financialSubObjectCode;
    }


    /**
     * Sets the financialSubObjectCode attribute value.
     * @param financialSubObjectCode The financialSubObjectCode to set.
     */
    public void setFinancialSubObjectCode(String financialSubObjectCode) {
        this.financialSubObjectCode = financialSubObjectCode;
    }


    /**
     * Gets the financialSubObjectCodeName attribute.
     * @return Returns the financialSubObjectCodeName.
     */
    public String getFinancialSubObjectCodeName() {
        return financialSubObjectCodeName;
    }


    /**
     * Sets the financialSubObjectCodeName attribute value.
     * @param financialSubObjectCodeName The financialSubObjectCodeName to set.
     */
    public void setFinancialSubObjectCodeName(String financialSubObjectCodeName) {
        this.financialSubObjectCodeName = financialSubObjectCodeName;
    }


    /**
     * Gets the financialSubObjectCdshortNm attribute.
     * @return Returns the financialSubObjectCdshortNm.
     */
    public String getFinancialSubObjectCdshortNm() {
        return financialSubObjectCdshortNm;
    }


    /**
     * Sets the financialSubObjectCdshortNm attribute value.
     * @param financialSubObjectCdshortNm The financialSubObjectCdshortNm to set.
     */
    public void setFinancialSubObjectCdshortNm(String financialSubObjectCdshortNm) {
        this.financialSubObjectCdshortNm = financialSubObjectCdshortNm;
    }


    /**
     * Gets the active attribute.
     * @return Returns the active.
     */
    public boolean isActive() {
        return active;
    }


    /**
     * Sets the active attribute value.
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }


    /**
     * Gets the universityFiscalYear attribute.
     * @return Returns the universityFiscalYear.
     */
    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }


    /**
     * Sets the universityFiscalYear attribute value.
     * @param universityFiscalYear The universityFiscalYear to set.
     */
    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }


    /**
     * Gets the chartOfAccounts attribute.
     * @return Returns the chartOfAccounts.
     */
    public Chart getChartOfAccounts() {
        return chartOfAccounts;
    }


    /**
     * Sets the chartOfAccounts attribute value.
     * @param chartOfAccounts The chartOfAccounts to set.
     */
    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }


    /**
     * Gets the account attribute.
     * @return Returns the account.
     */
    public Account getAccount() {
        return account;
    }


    /**
     * Sets the account attribute value.
     * @param account The account to set.
     */
    public void setAccount(Account account) {
        this.account = account;
    }


    /**
     * Gets the financialObject attribute.
     * @return Returns the financialObject.
     */
    public ObjectCode getFinancialObject() {
        return financialObject;
    }


    /**
     * Sets the financialObject attribute value.
     * @param financialObject The financialObject to set.
     */
    public void setFinancialObject(ObjectCode financialObject) {
        this.financialObject = financialObject;
    }


    /**
     * Gets the universityFiscal attribute.
     * @return Returns the universityFiscal.
     */
    public SystemOptions getUniversityFiscal() {
        return universityFiscal;
    }


    /**
     * Sets the universityFiscal attribute value.
     * @param universityFiscal The universityFiscal to set.
     */
    public void setUniversityFiscal(SystemOptions universityFiscal) {
        this.universityFiscal = universityFiscal;
    }


    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("documentNumber", this.documentNumber);
        m.put("universityFiscalYear", this.universityFiscalYear);
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("accountNumber", this.accountNumber);
        m.put("financialObjectCode", this.financialObjectCode);
        m.put("financialSubObjectCode", this.financialSubObjectCode);
        return m;
    }
}
