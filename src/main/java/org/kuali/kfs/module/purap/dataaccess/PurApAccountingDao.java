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
package org.kuali.kfs.module.purap.dataaccess;

import java.util.List;

import org.kuali.kfs.module.purap.businessobject.PurApItem;

/**
 * PurApAccounting DAO Interface.
 */
public interface PurApAccountingDao {

    /**
     * Retrieves the accounting lines for a purap item.
     * 
     * @param item - purap item
     * @return - list of accounting lines
     */
    public List getAccountingLinesForItem(PurApItem item);

    /**
     * Deletes the summary accounts by payment request document id.
     * 
     * @param paymentRequestIdentifier - payment request document id
     */
    public void deleteSummaryAccountsbyPaymentRequestIdentifier(Integer paymentRequestIdentifier);
    
    /**
     * Deletes the summary accounts by credit memo document id.
     * 
     * @param creditMemoIdentifier - credit memo document id
     */
    public void deleteSummaryAccountsbyCreditMemoIdentifier(Integer creditMemoIdentifier);
    
}
