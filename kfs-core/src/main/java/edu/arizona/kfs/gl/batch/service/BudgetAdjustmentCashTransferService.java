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
package edu.arizona.kfs.gl.batch.service;

public interface BudgetAdjustmentCashTransferService {
	/**
     * Reads an incoming file of general ledger transactions, extracts those transaction that qualify as budget adjustment transactions 
     * and saves those transactions to holding table GL_BUDGET_ADJUST_TRN_T.
     */
    public void extractAndSaveBudgetAdjustmentEntries();
    
    /**
     * Reads budget adjustment transactions from holding table GL_BUDGET_ADJUST_TRN_T and if needed, 
     * generates and posts budget adjustment cash transfer GL entries.
     */
    public void generateBudgetAdjustmentCashTransferTransactions();
}
