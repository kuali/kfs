/*
 * Copyright 2007 The Kuali Foundation
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
