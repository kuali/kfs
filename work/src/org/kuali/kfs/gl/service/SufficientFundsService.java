/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.gl.service;

import java.util.List;

import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.gl.businessobject.Transaction;
import org.kuali.kfs.sys.businessobject.SufficientFundsItem;
import org.kuali.kfs.sys.document.GeneralLedgerPostingDocument;


/**
 * Service used for manipulating disbursement voucher cover sheets.
 */
public interface SufficientFundsService {

    /**
     * Checks for sufficient funds on a single document
     * 
     * @param document document to check
     * @return Empty List if has sufficient funds for all accounts, List of SufficientFundsItem if not
     */
    public List<SufficientFundsItem> checkSufficientFunds(GeneralLedgerPostingDocument document);

    /**
     * Checks for sufficient funds on a list of transactions
     * 
     * @param transactions list of transactions
     * @return Empty List if has sufficient funds for all accounts, List of SufficientFundsItem if not
     */
    public List<SufficientFundsItem> checkSufficientFunds(List<? extends Transaction> transactions);

    /**
     * This operation derives the acct_sf_finobj_cd which is used to populate the General Ledger Pending entry table, so that later
     * we can do Suff Fund checking against that entry
     * 
     * @param financialObject the object code being checked against
     * @param accountSufficientFundsCode the kind of sufficient funds checking turned on in this system
     * @return the object code that should be used for the sufficient funds inquiry, or a blank String
     */
    public String getSufficientFundsObjectCode(ObjectCode financialObject, String accountSufficientFundsCode);

    /**
     * Purge the sufficient funds balance table by year/chart
     * 
     * @param chart chart of sufficient fund balances to purge
     * @param year fiscal year of sufficent fund balances to purge
     */
    public void purgeYearByChart(String chart, int year);
}
