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
package org.kuali.kfs.pdp.service;

import java.util.List;

import org.kuali.kfs.pdp.businessobject.PayeeACHAccount;

/**
 * Provides service methods for retrieving ACH information
 */
public interface AchService {

    /**
     * Retrieves a Payee's ACH record
     * 
     * @param idType type of payee
     * @param payeeId unique identifier for payee (based on type)
     * @param achTransactionType ach transaction type for record
     * @return PayeeAchAccount
     */
    public PayeeACHAccount getAchInformation(String idType, String payeeId, String achTransactionType);
    
    /**   
     * Gets all active PayeeAchAccounts.
     * @return all active PayeeAchAccounts.
     */
    public List<PayeeACHAccount> getActiveAchAccounts();
    
}
