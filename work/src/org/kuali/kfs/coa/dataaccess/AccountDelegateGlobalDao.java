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


public interface AccountDelegateGlobalDao {

    /**
     * 
     * This method returns the document number of any locking records.
     * @param lockingRepresentation String representation of the MaintenanceLock created by the AccountDelegateGlobal
     * @param documentNumber the document number of the Document being checked against.
     * @return The document number of the locking record, or null if none.
     */
    
    public String getLockingDocumentNumber(String lockingRepresentation, String documentNumber);
    
}
