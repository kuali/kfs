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
