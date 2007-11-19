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
package org.kuali.module.pdp.dao;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.kuali.module.pdp.bo.PaymentGroup;
import org.kuali.module.pdp.bo.PaymentProcess;

public interface PaymentGroupDao {
    /**
     * Get all the payment groups for a specific disbursement type & status code
     * 
     * @param disbursementType
     * @param paymentStatusCode
     * @return
     */
    public Iterator getByDisbursementTypeStatusCode(String disbursementType, String paymentStatusCode);

    /**
     * Get all the disbursement numbers for a specific process of a certain type
     * 
     * @param pid
     * @param disbursementType
     * @return
     */
    public List<Integer> getDisbursementNumbersByDisbursementType(Integer pid,String disbursementType);

    public Iterator getByProcessId(Integer pid);

    public Iterator getByProcess(PaymentProcess p);

    public void save(PaymentGroup pg);

    public PaymentGroup get(Integer id);

    public List getByBatchId(Integer batchId);

    public List getByDisbursementNumber(Integer disbursementNbr);
}
