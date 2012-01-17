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
package org.kuali.kfs.pdp.service;

import java.sql.Date;
import java.util.Iterator;
import java.util.List;

import org.kuali.kfs.pdp.businessobject.PaymentGroup;
import org.kuali.kfs.pdp.businessobject.PaymentProcess;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kns.service.DataDictionaryService;

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
    
    /**
     * Get all payment groups by Payment Process Id/Disbursement Type for a given bank code
     * 
     * @param pid
     * @param disbursementType
     * @param bankCode
     * @return
     */
    public abstract List<Integer> getDisbursementNumbersByDisbursementTypeAndBankCode(Integer pid,String disbursementType, String bankCode);
    
    /**
     * Given a process id and a disbursement type, finds a distinct list of bank codes used by payment groups within that payment process
     * @param pid payment process to query payment groups of
     * @param disbursementType the type of disbursements to query
     * @return a sorted List of bank codes
     */
    public abstract List<String> getDistinctBankCodesForProcessAndType(Integer pid, String disbursementType);
    
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
    
    /**
     * Gets the sort group id
     * 
     * @param paymentGroup
     * @return
     */
    public int getSortGroupId(PaymentGroup paymentGroup);
    
    /**
     * Gets the sort group name
     * 
     * @param sortGroupId
     * @return
     */
    public String getSortGroupName(int sortGroupId);
    
    /**
     * Sets the parameter service
     * 
     * @param parameterService
     */
    public void setParameterService(ParameterService parameterService);
    
    /**
     * Sets DataDictionaryService
     * 
     * @param dataDictionaryService
     */
    public void setDataDictionaryService(DataDictionaryService dataDictionaryService);
    
    /**
     * Gets list of ach payments in which an advice notification has not been sent
     * 
     * @return List<PaymentGroup>
     */
    public List<PaymentGroup> getAchPaymentsNeedingAdviceNotification();
}
