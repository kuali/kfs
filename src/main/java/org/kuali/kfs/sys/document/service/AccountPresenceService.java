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
package org.kuali.kfs.sys.document.service;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.ObjectCode;

/**
 * This service interface defines methods that an AccountPresenceService implementation must provide.
 * 
 */
public interface AccountPresenceService {

    /**
     * Checks the given account for presence control turned on. If turned on, the object code must have a budget record in the gl
     * balance table, otherwise this method returns false. If presence control is turned off, method returns true.
     * 
     * @param account The account to be checked for presense control.
     * @param objectCode The object code to be looked up in the gl balance table.
     * @return True if presence control is turned on and obj code is in gl balance table, false otherwise.
     */
    public boolean isObjectCodeBudgetedForAccountPresence(Account account, ObjectCode objectCode);
}
