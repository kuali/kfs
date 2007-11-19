/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.pdp.service;

import java.sql.Date;
import java.util.Iterator;
import java.util.List;

import org.kuali.module.pdp.bo.PaymentGroup;
import org.kuali.module.pdp.bo.PaymentProcess;

public interface PaymentGroupService {
    /**
     * Get all payment groups by a disbursement type code and status code
     * 
     * @param disbursementType
     * @param paymentStatusCode
     * @return
     */
    public Iterator getByDisbursementTypeStatusCode(String disbursementType, String paymentStatusCode);

    /**
     * Get all payment groups by Payment Process Id This method...
     * 
     * @param processId
     * @return
     */
    public Iterator getByProcessId(Integer processId);

    /**
     * Get all payment groups by Payment Process object
     * 
     * @param p
     * @return
     */
    public Iterator getByProcess(PaymentProcess p);

    /**
     * Get all payment groups by Payment Process Id/Disbursement Type
     * 
     * @param pid
     * @param disbursementType
     * @return
     */
    public List<Integer> getDisbursementNumbersByDisbursementType(Integer pid,String disbursementType);

    public void save(PaymentGroup pg);

    public PaymentGroup get(Integer id);

    public List getByBatchId(Integer batchId);

    public List getByDisbursementNumber(Integer disbursementNbr);

    /**
     * Mark a paid group as processed
     * 
     * @param group
     * @param processDate
     */
    public void processPaidGroup(PaymentGroup group, Date processDate);

    /**
     * Mark a cancelled group as processed
     * 
     * @param group
     * @param processDate
     */
    public void processCancelledGroup(PaymentGroup group, Date processDate);
}
