/*
 * Copyright 2006-2008 The Kuali Foundation
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

package org.kuali.kfs.module.purap.businessobject;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;

import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.util.ObjectPopulationUtils;
import org.kuali.rice.core.api.datetime.DateTimeService;


/**
 * Credit Memo Account Revision Business Object.
 */
public class CreditMemoAccountRevision extends CreditMemoAccount {

    protected Integer accountRevisionIdentifier;
    private Timestamp accountRevisionTimestamp;

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
    public CreditMemoAccountRevision() {
    }

    /**
     * Constructor.
     *
     * @param account - credit memo account
     */
    public CreditMemoAccountRevision(CreditMemoAccount cma, Integer postingYear, String postingPeriodCode) {
        // copy base attributes
        ObjectPopulationUtils.populateFromBaseWithSuper(cma, this, new HashMap<String, Class<?>>(), new HashSet<Class>());
        this.setAccountRevisionTimestamp(SpringContext.getBean(DateTimeService.class).getCurrentTimestamp());
        this.setPostingYear(postingYear);
        this.setPostingPeriodCode(postingPeriodCode);
    }

    public Integer getAccountRevisionIdentifier() {
        return accountRevisionIdentifier;
    }

    public void setAccountRevisionIdentifier(Integer accountRevisionIdentifier) {
        this.accountRevisionIdentifier = accountRevisionIdentifier;
    }

    public Timestamp getAccountRevisionTimestamp() {
        return accountRevisionTimestamp;
    }

    public void setAccountRevisionTimestamp(Timestamp accountRevisionTimestamp) {
        this.accountRevisionTimestamp = accountRevisionTimestamp;
    }

}
