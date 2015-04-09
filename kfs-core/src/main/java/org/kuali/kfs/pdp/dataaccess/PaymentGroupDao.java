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
package org.kuali.kfs.pdp.dataaccess;

import java.util.List;

import org.kuali.kfs.pdp.businessobject.PaymentGroup;

public interface PaymentGroupDao {

    /**
     * Get all the disbursement numbers for a specific process of a certain type
     * 
     * @param pid
     * @param disbursementType
     * @return
     */
    public List<Integer> getDisbursementNumbersByDisbursementType(Integer pid, String disbursementType);
    
    /**
     * Get all the disbursement numbers for a specific process of a certain type for a certain bank
     * 
     * @param pid
     * @param disbursementType
     * @param bankCode the bank code to find disbursement numbers for
     * @return
     */
    public abstract List<Integer> getDisbursementNumbersByDisbursementTypeAndBankCode(Integer pid, String disbursementType, String bankCode);

    /**
     * Gets list of ach payments in which an advice notification has not been sent
     * 
     * @return List<PaymentGroup>
     */
    public List<PaymentGroup> getAchPaymentsNeedingAdviceNotification();
    
    /**
     * Given a process id and a disbursement type, finds a distinct list of bank codes used by payment groups within that payment process
     * @param pid payment process to query payment groups of
     * @param disbursementType the type of disbursements to query
     * @return a sorted List of bank codes
     */
    public abstract List<String> getDistinctBankCodesForProcessAndType(Integer pid, String disbursementType);

}
