/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/purap/businessobject/PaymentRequestThresholdNotToExceed.java,v $
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

package org.kuali.module.purap.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.chart.bo.Chart;

/**
 * 
 */
public class PaymentRequestThresholdNotToExceed extends BusinessObjectBase {

	private String chartOfAccountsCode;
	private KualiDecimal accountsPayableThresholdNotToExceedAmount;

    private Chart chartOfAccounts;

	/**
	 * Default constructor.
	 */
	public PaymentRequestThresholdNotToExceed() {

	}

	/**
	 * Gets the chartOfAccountsCode attribute.
	 * 
	 * @return Returns the chartOfAccountsCode
	 * 
	 */
	public String getChartOfAccountsCode() { 
		return chartOfAccountsCode;
	}

	/**
	 * Sets the chartOfAccountsCode attribute.
	 * 
	 * @param chartOfAccountsCode The chartOfAccountsCode to set.
	 * 
	 */
	public void setChartOfAccountsCode(String chartOfAccountsCode) {
		this.chartOfAccountsCode = chartOfAccountsCode;
	}


	/**
	 * Gets the accountsPayableThresholdNotToExceedAmount attribute.
	 * 
	 * @return Returns the accountsPayableThresholdNotToExceedAmount
	 * 
	 */
	public KualiDecimal getAccountsPayableThresholdNotToExceedAmount() { 
		return accountsPayableThresholdNotToExceedAmount;
	}

	/**
	 * Sets the accountsPayableThresholdNotToExceedAmount attribute.
	 * 
	 * @param accountsPayableThresholdNotToExceedAmount The accountsPayableThresholdNotToExceedAmount to set.
	 * 
	 */
	public void setAccountsPayableThresholdNotToExceedAmount(KualiDecimal accountsPayableThresholdNotToExceedAmount) {
		this.accountsPayableThresholdNotToExceedAmount = accountsPayableThresholdNotToExceedAmount;
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
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
	    return m;
    }
}
