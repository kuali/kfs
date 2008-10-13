/*
 * Copyright 2006-2007 The Kuali Foundation.
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

package org.kuali.kfs.module.purap.businessobject;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;

import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.module.purap.util.PurApObjectUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.DateTimeService;


/**
 * Credit Memo Account History Business Object.
 */
public class CreditMemoAccountHistory extends CreditMemoAccount {

    protected Integer accountHistoryIdentifier;
    private Timestamp accountHistoryTimestamp;

    private AccountingPeriod accountingPeriod;
    
    public AccountingPeriod getAccountingPeriod() {
		return accountingPeriod;
	}

	public void setAccountingPeriod(AccountingPeriod accountingPeriod) {
		this.accountingPeriod = accountingPeriod;
	}

	/**
     * Default constructor.
     */
    public CreditMemoAccountHistory() {
    }

    /**
     * Constructor.
     * 
     * @param account - credit memo account
     */
    public CreditMemoAccountHistory(CreditMemoAccount cma, Integer postingYear, String postingPeriodCode) {
        // copy base attributes
        PurApObjectUtils.populateFromBaseWithSuper(cma, this, new HashMap<String, Class<?>>(), new HashSet<Class>());
        this.setAccountHistoryTimestamp(SpringContext.getBean(DateTimeService.class).getCurrentTimestamp());
        this.setPostingYear(postingYear);
        this.setPostingPeriodCode(postingPeriodCode);
    }

    public Integer getAccountHistoryIdentifier() {
        return accountHistoryIdentifier;
    }

    public void setAccountHistoryIdentifier(Integer accountHistoryIdentifier) {
        this.accountHistoryIdentifier = accountHistoryIdentifier;
    }

    public Timestamp getAccountHistoryTimestamp() {
        return accountHistoryTimestamp;
    }

    public void setAccountHistoryTimestamp(Timestamp accountHistoryTimestamp) {
        this.accountHistoryTimestamp = accountHistoryTimestamp;
    }

}
