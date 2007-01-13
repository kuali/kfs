/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.gl.service;

import java.util.List;

import org.kuali.core.document.TransactionalDocument;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.util.SufficientFundsItem;


/**
 * Service used for manipulating disbursement voucher cover sheets.
 * 
 * 
 */
public interface SufficientFundsService {

    /**
     * This method checks for sufficient funds on a single document
     * 
     * @param document document
     * @return Empty List if has sufficient funds for all accounts, List of SufficientFundsItem if not
     */
    public List<SufficientFundsItem> checkSufficientFunds(TransactionalDocument document);

    /**
     * This method checks for sufficient funds on a list of transactions
     * 
     * @param document document
     * @return Empty List if has sufficient funds for all accounts, List of SufficientFundsItem if not
     */
    public List<SufficientFundsItem> checkSufficientFunds(List<? extends Transaction> transactions);

    /**
     * This operation derives the acct_sf_finobj_cd which is used to populate the General Ledger Pending entry table, so that later
     * we can do Suff Fund checking against that entry
     * 
     * @param financialObject
     * @param accountSufficientFundsCode
     * @return
     */
    public String getSufficientFundsObjectCode(ObjectCode financialObject, String accountSufficientFundsCode);

    /**
     * Purge the sufficient funds balance table by year/chart
     * 
     * @param chart
     * @param year
     */
    public void purgeYearByChart(String chart, int year);
}
