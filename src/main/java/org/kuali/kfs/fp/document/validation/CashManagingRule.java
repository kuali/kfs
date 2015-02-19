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
package org.kuali.kfs.fp.document.validation;

import org.kuali.kfs.fp.businessobject.CashDrawer;
import org.kuali.kfs.fp.businessobject.CashieringTransaction;

/**
 * Callbacks for rules that the Cash Management document checks
 */
public interface CashManagingRule {
    
    /**
     * Validates a cashiering transaction before it is added to a document
     * @param cashDrawer the cash drawer the transaction will be applied to
     * @param cashieringTransaction the cashering transaction which may be applied
     * @return true if the transaction would be valid, false otherwise
     */
    public abstract boolean processCashieringTransactionApplication(CashDrawer cashDrawer, CashieringTransaction cashieringTransaction);
}
