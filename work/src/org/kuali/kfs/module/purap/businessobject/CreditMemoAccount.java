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

package org.kuali.module.purap.bo;


/**
 * Accounting line Business Object for a credit memo item line.
 */
public class CreditMemoAccount extends PurApAccountingLineBase {

    private CreditMemoItem creditMemoItem;

    /**
     * Default constructor.
     */
    public CreditMemoAccount() {

    }

    /**
     * Constructs a Credit Memo Account object from an existing PurAp Accounting Line object.
     * 
     * @param accountingLine the accounting line to copy from.
     */
    public CreditMemoAccount(PurApAccountingLineBase accountingLine) {
        super();

        setChartOfAccountsCode(accountingLine.getChartOfAccountsCode());
        setAccountNumber(accountingLine.getAccountNumber());
        setSubAccountNumber(accountingLine.getSubAccountNumber());
        setFinancialObjectCode(accountingLine.getFinancialObjectCode());
        setFinancialSubObjectCode(accountingLine.getFinancialSubObjectCode());
        setProjectCode(accountingLine.getProjectCode());
        setOrganizationReferenceId(accountingLine.getOrganizationReferenceId());
        setAmount(accountingLine.getAmount());
        setAccountLinePercent(accountingLine.getAccountLinePercent());
    }

    public CreditMemoItem getCreditMemoItem() {
        return creditMemoItem;
    }

    /**
     * @deprecated
     */
    public void setCreditMemoItem(CreditMemoItem creditMemoItem) {
        this.creditMemoItem = creditMemoItem;
    }
}
