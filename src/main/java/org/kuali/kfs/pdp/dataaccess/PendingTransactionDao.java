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
/*
 * Created on Sep 2, 2004
 *
 */
package org.kuali.kfs.pdp.dataaccess;

import java.util.Iterator;

import org.kuali.kfs.pdp.businessobject.GlPendingTransaction;

/**
 * DAO Interface for <code>GlPendingTransaction</code>
 */
public interface PendingTransactionDao {

    /**
     * Get all of the GL transactions where the extract flag is null
     * 
     * @return Iterator of all the transactions
     */
    public Iterator<GlPendingTransaction> getUnextractedTransactions();
    
    /**
     * Deletes all transaction records where the process indicator is true
     */
    public void clearExtractedTransactions();
}
