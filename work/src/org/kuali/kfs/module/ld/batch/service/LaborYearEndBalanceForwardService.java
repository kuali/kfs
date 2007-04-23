/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.labor.service;

/**
 * Labor Ledger Year End – Inception to Date Beginning Balance process moves the Year-to-Date Total plus the Contracts and Grants
 * Beginning Balances to the Contracts and Grants Beginning Balances of the new fiscal year for a designated group of accounts (to
 * be identified by fund group and sub fund group).
 */
public interface LaborYearEndBalanceForwardService {

    /**
     * moves the Year-to-Date Total plus the Contracts and Grants Beginning Balances to the Contracts and Grants Beginning Balances
     * of the new fiscal year for a designated group of accounts. The fiscal year is given through application parameter table. The
     * new fiscal year is (fiscalYear + 1) by default.
     */
    public void forwardBalance();

    /**
     * moves the Year-to-Date Total plus the Contracts and Grants Beginning Balances to the Contracts and Grants Beginning Balances
     * of the new fiscal year for a designated group of accounts
     * 
     * @param fiscalYear the fiscal year to be processed. The new fiscal year is (fiscalYear + 1) by default.
     */
    public void forwardBalance(Integer fiscalYear);

    /**
     * moves the Year-to-Date Total plus the Contracts and Grants Beginning Balances to the Contracts and Grants Beginning Balances
     * of the new fiscal year for a designated group of accounts
     * 
     * @param fiscalYear the fiscal year to be processed.
     * @param newFiscalYear the new fiscal year
     */
    public void forwardBalance(Integer fiscalYear, Integer newFiscalYear);
}