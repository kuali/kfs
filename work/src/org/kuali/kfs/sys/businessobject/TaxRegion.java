/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.sys.businessobject;

import java.sql.Date;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCodeCurrent;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

public class TaxRegion extends PersistableBusinessObjectBase implements MutableInactivatable {

    protected String taxRegionCode; // (e.g., state code or district code)
    protected String taxRegionName; // (e.g., state name or tax district name)
    protected String taxRegionTypeCode;
    protected String chartOfAccountsCode;
    protected String accountNumber;
    protected String financialObjectCode;
    protected boolean active;
    protected boolean taxRegionUseTaxIndicator;

    protected Chart chartOfAccounts;
    protected Account account;
    protected ObjectCodeCurrent objectCode;
    protected TaxRegionType taxRegionType;

    protected List<TaxRegionRate> taxRegionRates = new ArrayList<TaxRegionRate>();
    protected List<TaxRegionState> taxRegionStates = new ArrayList<TaxRegionState>();
    protected List<TaxRegionCounty> taxRegionCounties = new ArrayList<TaxRegionCounty>();
    protected List<TaxRegionPostalCode> taxRegionPostalCodes = new ArrayList<TaxRegionPostalCode>();

    public List<TaxRegionRate> getTaxRegionRates() {
        return taxRegionRates;
    }

    public void setTaxRegionRates(List<TaxRegionRate> taxRegionRates) {
        this.taxRegionRates = taxRegionRates;
    }

    public List<TaxRegionState> getTaxRegionStates() {
        return taxRegionStates;
    }

    public void setTaxRegionStates(List<TaxRegionState> taxRegionStates) {
        this.taxRegionStates = taxRegionStates;
    }

    public List<TaxRegionCounty> getTaxRegionCounties() {
        return taxRegionCounties;
    }

    public void setTaxRegionCounties(List<TaxRegionCounty> taxRegionCounties) {
        this.taxRegionCounties = taxRegionCounties;
    }

    public List<TaxRegionPostalCode> getTaxRegionPostalCodes() {
        return taxRegionPostalCodes;
    }

    public void setTaxRegionPostalCodes(List<TaxRegionPostalCode> taxRegionPostalCodes) {
        this.taxRegionPostalCodes = taxRegionPostalCodes;
    }

    public TaxRegionType getTaxRegionType() {
        return taxRegionType;
    }

    public void setTaxRegionType(TaxRegionType taxRegionType) {
        this.taxRegionType = taxRegionType;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    public String getFinancialObjectCode() {
        return financialObjectCode;
    }

    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }

    public String getTaxRegionCode() {
        return taxRegionCode;
    }

    public void setTaxRegionCode(String taxDistrictCode) {
        this.taxRegionCode = taxDistrictCode;
    }

    public String getTaxRegionName() {
        return taxRegionName;
    }

    public void setTaxRegionName(String taxDistrictName) {
        this.taxRegionName = taxDistrictName;
    }


    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String toString() {
        return taxRegionTypeCode + "-" + taxRegionCode + "-" + taxRegionName;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Chart getChartOfAccounts() {
        return chartOfAccounts;
    }

    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    public ObjectCodeCurrent getObjectCode() {
        return objectCode;
    }

    public void setObjectCode(ObjectCodeCurrent objectCode) {
        this.objectCode = objectCode;
    }

    public String getTaxRegionTypeCode() {
        return taxRegionTypeCode;
    }

    public void setTaxRegionTypeCode(String taxRegionTypeCode) {
        this.taxRegionTypeCode = taxRegionTypeCode;
    }

    public boolean isTaxRegionUseTaxIndicator() {
        return taxRegionUseTaxIndicator;
    }

    public void setTaxRegionUseTaxIndicator(boolean taxRegionUseTaxIndicator) {
        this.taxRegionUseTaxIndicator = taxRegionUseTaxIndicator;
    }

    /**
     * This method returns the effective tax region rate based off the date of transaction passed in
     * @param dateOfTransaction
     * @return
     */
    public TaxRegionRate getEffectiveTaxRegionRate(Date dateOfTransaction) {
        TaxRegionRate selectedTaxRegionRate = null;

        for (TaxRegionRate taxRegionRate : taxRegionRates) {
            if (taxRegionRate.getEffectiveDate().getTime() <= dateOfTransaction.getTime() ) {
                selectedTaxRegionRate = taxRegionRate;
            }
        }

        return selectedTaxRegionRate;
    }
}
