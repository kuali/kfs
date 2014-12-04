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
package org.kuali.kfs.coa.dataaccess;

import java.sql.Date;
import java.util.Iterator;

import org.kuali.kfs.coa.businessobject.AccountDelegate;


public interface AccountDelegateDao {

    /**
     * Retrieves the Document Number of any AccountDelegate locking this record.
     * 
     * @param lockingRepresentation String representation of the MaintanceLock to check against.
     * @param documentNumber the document number being checked against.
     * @return the document number of the Document locking this record.
     */

    public String getLockingDocumentNumber(String lockingRepresentation, String documentNumber);

    /**
     * Retrieves all active account delegates which reference the given user
     * 
     * @param principalId a principal ID of the person to find account delegations for
     * @param primary whether the account delegates returned should be primary or not
     * @return a Collection, presumably of AccountDelegates
     */
    public abstract Iterator<AccountDelegate> getAccountDelegationsForPerson(String principalId, boolean primary);

    /**
     * Determines if the given principal is an active delegate for any non-closed account
     * 
     * @param principalId the principal ID to check primary account delegations for
     * @param currentSqlDate
     * @return true if the principal is a primary account delegate, false otherwise
     */
    public abstract boolean isPrincipalInAnyWayShapeOrFormPrimaryAccountDelegate(String principalId, Date currentSqlDate);

    /**
     * Determines if the given principal is an active delegate for any non-closed account
     * 
     * @param principalId the principal ID to check secondary account delegations for
     * @param currentSqlDate current Sql date
     * @return true if the principal is a secondary account delegate, false otherwise
     */
    public abstract boolean isPrincipalInAnyWayShapeOrFormSecondaryAccountDelegate(String principalId, Date currentSqlDate);
}
