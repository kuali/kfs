/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.sys.businessobject;

import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCodeCurrent;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.util.TypedArrayList;

public class TaxRegion extends PersistableBusinessObjectBase {

    private String taxRegionCode; //(e.g., state code or district code)
    private String taxRegionName; //(e.g., state name or tax district name)
    private String taxRegionTypeCode;
    private String taxRegionUseTaxIndicator;
    private String chartOfAccountsCode;
    private String accountNumber;
    private String financialObjectCode;
    private boolean active;
    
    private Chart chartOfAccounts;
    private Account account;
    private ObjectCodeCurrent objectCode;
    
    private List<TaxRegionRate> taxRegionRates = new TypedArrayList(TaxRegionRate.class);
    
    public List<TaxRegionRate> getTaxRegionRates() {
        return taxRegionRates;
    }

    public void setTaxRegionRates(List<TaxRegionRate> taxRegionRates) {
        this.taxRegionRates = taxRegionRates;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public boolean isActive() {
        return active;
    }

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

    @Override
    protected LinkedHashMap toStringMapper() {
        // TODO Auto-generated method stub
        return null;
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

	public String getTaxRegionUseTaxIndicator() {
		return taxRegionUseTaxIndicator;
	}

	public void setTaxRegionUseTaxIndicator(String taxRegionUseTaxIndicator) {
		this.taxRegionUseTaxIndicator = taxRegionUseTaxIndicator;
	}

}
