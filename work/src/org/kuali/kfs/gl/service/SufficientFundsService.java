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
