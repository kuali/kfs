/*
 * Copyright 2009 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
