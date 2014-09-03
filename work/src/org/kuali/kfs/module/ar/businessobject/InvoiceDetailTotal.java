/*
 * Copyright 2014 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.businessobject;

import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * Convenience class which holds totals from invoice details
 * This class is not persisted
 */
public class InvoiceDetailTotal {
    protected KualiDecimal budget = KualiDecimal.ZERO;
    protected KualiDecimal cumulative = KualiDecimal.ZERO;
    protected KualiDecimal expenditures = KualiDecimal.ZERO;
    protected KualiDecimal balance = KualiDecimal.ZERO;
    protected KualiDecimal billed = KualiDecimal.ZERO;

    public KualiDecimal getBudget() {
        return budget;
    }
    public KualiDecimal getCumulative() {
        return cumulative;
    }
    public KualiDecimal getExpenditures() {
        return expenditures;
    }
    public KualiDecimal getBalance() {
        return balance;
    }
    public KualiDecimal getBilled() {
        return billed;
    }

    public void sumInvoiceDetail(ContractsGrantsInvoiceDetail contractsGrantsInvoiceDetail) {
        if (null != contractsGrantsInvoiceDetail.getBudget()) {
            budget = budget.add(contractsGrantsInvoiceDetail.getBudget());
        }
        if (null != contractsGrantsInvoiceDetail.getCumulative()) {
            cumulative = cumulative.add(contractsGrantsInvoiceDetail.getCumulative());
        }
        if (null != contractsGrantsInvoiceDetail.getExpenditures()) {
            expenditures = expenditures.add(contractsGrantsInvoiceDetail.getExpenditures());
        }
        if (null != contractsGrantsInvoiceDetail.getBalance()) {
            balance = balance.add(contractsGrantsInvoiceDetail.getBalance());
        }
        if (null != contractsGrantsInvoiceDetail.getBilled()) {
            billed = billed.add(contractsGrantsInvoiceDetail.getBilled());
        }
    }
}
