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
package org.kuali.kfs.coa.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.sys.businessobject.FiscalYearBasedBusinessObject;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.impl.PersistenceStructureServiceImpl;

/**
 * 
 */
public class SubObjectCode extends PersistableBusinessObjectBase implements MutableInactivatable, FiscalYearBasedBusinessObject {

    private static final long serialVersionUID = -5292158248714650271L;

    static {
        PersistenceStructureServiceImpl.referenceConversionMap.put(SubObjectCode.class, SubObjectCodeCurrent.class);
    }

    /**
     * Default no-arg constructor.
     */
    public SubObjectCode() {

    }

    /**
     * Constructs an active SubObjCd.java with the given primary key.
     * 
     * @param universityFiscalYear
     * @param chartOfAccountsCode
     * @param accountNumber
     * @param financialObjectCode
     * @param financialSubObjectCode
     */
    public SubObjectCode(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String financialObjectCode, String financialSubObjectCode) {
        this.universityFiscalYear = universityFiscalYear;
        this.chartOfAccountsCode = chartOfAccountsCode;
        this.accountNumber = accountNumber;
        this.financialObjectCode = financialObjectCode;
        this.financialSubObjectCode = financialSubObjectCode;
        this.active = true;
    }

    private String chartOfAccountsCode;
    private String accountNumber;
    private String financialObjectCode;
    private String financialSubObjectCode;
    private String financialSubObjectCodeName;
    private String financialSubObjectCdshortNm;
    private boolean active;
    private Integer universityFiscalYear;

    private Chart chartOfAccounts;
    private Account account;
    private ObjectCode financialObject;
    private SystemOptions universityFiscal;

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
     * @deprecated
     */
    public void setFinancialObject(ObjectCode financialObject) {
        this.financialObject = financialObject;
    }

    /**
     * Gets the financialSubObjectCodeName attribute.
     * 
     * @return Returns the financialSubObjectCodeName
     */
    public String getFinancialSubObjectCodeName() {
        return financialSubObjectCodeName;
    }

    /**
     * Sets the financialSubObjectCodeName attribute.
     * 
     * @param financialSubObjectCodeName The financialSubObjectCodeName to set.
     */
    public void setFinancialSubObjectCodeName(String financialSubObjectCodeName) {
        this.financialSubObjectCodeName = financialSubObjectCodeName;
    }

    /**
     * Gets the financialSubObjectCdshortNm attribute.
     * 
     * @return Returns the financialSubObjectCdshortNm
     */
    public String getFinancialSubObjectCdshortNm() {
        return financialSubObjectCdshortNm;
    }

    /**
     * Sets the financialSubObjectCdshortNm attribute.
     * 
     * @param financialSubObjectCdshortNm The financialSubObjectCdshortNm to set.
     */
    public void setFinancialSubObjectCdshortNm(String financialSubObjectCdshortNm) {
        this.financialSubObjectCdshortNm = financialSubObjectCdshortNm;
    }

    /**
     * Gets the active attribute.
     * 
     * @return Returns the active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute.
     * 
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Gets the universityFiscal attribute.
     * 
     * @return Returns the universityFiscal
     */
    public SystemOptions getUniversityFiscal() {
        return universityFiscal;
    }

    /**
     * Sets the universityFiscal attribute.
     * 
     * @param universityFiscal The universityFiscal to set.
     * @deprecated
     */
    public void setUniversityFiscal(SystemOptions universityFiscal) {
        this.universityFiscal = universityFiscal;
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
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();

        m.put("universityFiscalYear", this.universityFiscalYear);
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("accountNumber", this.accountNumber);
        m.put("financialObjectCode", this.financialObjectCode);
        m.put("financialSubObjectCode", this.financialSubObjectCode);

        return m;
    }

}
