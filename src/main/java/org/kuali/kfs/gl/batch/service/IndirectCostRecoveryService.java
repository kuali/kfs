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
package org.kuali.kfs.gl.batch.service;

import org.kuali.kfs.gl.businessobject.Transaction;
import org.kuali.kfs.sys.service.ReportWriterService;

/**
 * An interface with a predicate that tells if a transaction with the given fields would be an ICR transaction or not
 */
public interface IndirectCostRecoveryService {
    /**
     * This will determine if this transaction is an ICR eligible transaction
     * 
     * @param transaction the transaction which is being determined to be ICR or not
     * @param objectType the object type of the transaction
     * @param account the account of the transaction
     * @param objectCode the object code of the transaction
     * @return true if the transaction is an ICR transaction and therefore should have an expenditure transaction created for it; false if otherwise
     */
    public abstract boolean isIcrTransaction(Transaction transaction, ReportWriterService reportWriterService);
}
