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

package org.kuali.kfs.pdp.businessobject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.pdp.service.PaymentGroupService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.krad.bo.TransientBusinessObjectBase;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * This class collects the summary information for payment Format Process
 */
public class FormatProcessSummary extends TransientBusinessObjectBase {

    private List<ProcessSummary> processSummaryList;
    private KualiInteger totalCount;
    private KualiDecimal totalAmount;
    private KualiInteger processId;

    /**
     * Constructs a FormatProcessSummary.java.
     */
    public FormatProcessSummary() {
        super();
        processSummaryList = new ArrayList<ProcessSummary>();
        totalCount = KualiInteger.ZERO;
        totalAmount = KualiDecimal.ZERO;
    }

    /**
     * Add the payment detail info to our summary list
     * 
     * @param paymentGroup
     */
    public void add(PaymentGroup paymentGroup) {
        ProcessSummary ps = findProcessSummary(paymentGroup);

        // If it's not in our list, add it
        if (ps == null) {
            ps = new ProcessSummary();
            ps.setBeginDisbursementNbr(KualiInteger.ZERO);
            ps.setCustomer(paymentGroup.getBatch().getCustomerProfile());
            ps.setDisbursementType(paymentGroup.getDisbursementType());
            ps.setEndDisbursementNbr(KualiInteger.ZERO);
            ps.setProcess(paymentGroup.getProcess());
            ps.setProcessTotalAmount(KualiDecimal.ZERO);
            ps.setProcessTotalCount(KualiInteger.ZERO);
            ps.setSortGroupId(new KualiInteger(SpringContext.getBean(PaymentGroupService.class).getSortGroupId(paymentGroup)));
            processSummaryList.add(ps);
            
            // if first one added set the process id
            if (processId == null) {
                processId = paymentGroup.getProcessId();
            }
        }

        // Update the total & count
        ps.setProcessTotalAmount(ps.getProcessTotalAmount().add(paymentGroup.getNetPaymentAmount()));
        ps.setProcessTotalCount(new KualiInteger(ps.getProcessTotalCount().intValue() + paymentGroup.getPaymentDetails().size()));
        totalAmount = totalAmount.add(paymentGroup.getNetPaymentAmount());
        totalCount = totalCount.add(new KualiInteger(paymentGroup.getPaymentDetails().size()));
    }
    
    /**
     * Save all the process summary records
     * 
     * @param pdd
     */
    public void save() {
        for (Iterator<ProcessSummary> iter = processSummaryList.iterator(); iter.hasNext();) {
            ProcessSummary ps = (ProcessSummary) iter.next();
            
            SpringContext.getBean(BusinessObjectService.class).save(ps);
        }
    }

    /**
     * This method checks if we already have a summary record. 
     * @param paymentGroup
     * @return If we we already have a summary record return it, if not, return null;
     */
    private ProcessSummary findProcessSummary(PaymentGroup paymentGroup) {

        for (Iterator<ProcessSummary> iter = processSummaryList.iterator(); iter.hasNext();) {
            ProcessSummary processSummary = (ProcessSummary) iter.next();
            
            if(ObjectUtils.equals(processSummary.getCustomer(), paymentGroup.getBatch().getCustomerProfile()) && ObjectUtils.equals(processSummary.getDisbursementType(), paymentGroup.getDisbursementType()) && (processSummary.getSortGroupId().intValue()==SpringContext.getBean(PaymentGroupService.class).getSortGroupId(paymentGroup)) && ObjectUtils.equals(processSummary.getProcess(), paymentGroup.getProcess())) {
                return processSummary;
            }
        }
        return null;
    }

    /**
     * @param pg Update the disbursement number information
     * @param range
     */
    public void setDisbursementNumber(PaymentGroup paymentGroup, Integer disbursementNumber) {
        ProcessSummary processSummary = findProcessSummary(paymentGroup);
        if (processSummary != null) {
            if (processSummary.getBeginDisbursementNbr().isZero()) {
                processSummary.setBeginDisbursementNbr(new KualiInteger(disbursementNumber));
                processSummary.setEndDisbursementNbr(new KualiInteger(disbursementNumber));
            }
            else {
                processSummary.setEndDisbursementNbr(new KualiInteger(disbursementNumber));
            }
        }
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    @SuppressWarnings("rawtypes")
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap<String, List<ProcessSummary>> m = new LinkedHashMap<String, List<ProcessSummary>>();

        m.put(PdpPropertyConstants.FormatProcessSummary.PROCESS_SUMMARY, this.processSummaryList);

        return m;
    }

    /**
     * This method gets the total amount.
     * @return totalAmount
     */
    public KualiDecimal getTotalAmount() {
        return totalAmount;
    }

    /**
     * This method sets the total amount.
     * @param totalAmount
     */
    public void setTotalAmount(KualiDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    /**
     * This method gets the total count.
     * @return totalCount
     */
    public KualiInteger getTotalCount() {
        return totalCount;
    }

    /**
     * This method sets the total count.
     * @param totalCount
     */
    public void setTotalCount(KualiInteger totalCount) {
        this.totalCount = totalCount;
    }

    /**
     * This method retrieves a specific process summary from the list, by index
     * 
     * @param index the index of the results to retrieve the process summary from
     * @return a ProcessSummary
     */
    public ProcessSummary getProcessSummary(int index) {
        if (index >= processSummaryList.size()) {
            for (int i = processSummaryList.size(); i <= index; i++) {
                processSummaryList.add(new ProcessSummary());
            }
        }
        return (ProcessSummary) processSummaryList.get(index);
    }

    /**
     * This method sets a process summary value at a given index in the process summary list.
     * 
     * @param key the index
     * @param value the new value
     */
    public void setProcessSummary(int key, ProcessSummary value) {
        processSummaryList.set(key, value);
    }

    /**
     * This method gets the process summary list
     * @return processSummaryList
     */
    public List<ProcessSummary> getProcessSummaryList() {
        return processSummaryList;
    }

    /**
     * This method sets the process summary list.
     * @param processSummaryList
     */
    public void setProcessSummaryList(List<ProcessSummary> processSummaryList) {
        this.processSummaryList = processSummaryList;
    }

    /**
     * This method gets the process id.
     * @return the processId
     */
    public KualiInteger getProcessId() {
        return processId;
    }

    /**
     * This method sets the process id.
     * @param processId
     */
    public void setProcessId(KualiInteger processId) {
        this.processId = processId;
    }
}
