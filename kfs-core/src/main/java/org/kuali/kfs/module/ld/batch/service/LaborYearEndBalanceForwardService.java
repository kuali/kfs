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
package org.kuali.kfs.module.ld.batch.service;

/**
 * Labor Ledger Year End - Inception to Date Beginning Balance process moves the Year-to-Date Total plus the Contracts and Grants
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
