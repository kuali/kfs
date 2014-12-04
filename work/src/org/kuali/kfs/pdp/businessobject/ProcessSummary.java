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
/*
 * Created on Aug 19, 2004
 *
 */
package org.kuali.kfs.pdp.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.pdp.service.PaymentGroupService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.TimestampedBusinessObjectBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiInteger;

/**
 * 
 */
public class ProcessSummary extends TimestampedBusinessObjectBase {
    private KualiInteger id;
    private KualiInteger customerId;
    private String disbursementTypeCode;
    private KualiInteger processId;
    private KualiInteger sortGroupId;
    private KualiInteger beginDisbursementNbr;
    private KualiInteger endDisbursementNbr;
    private KualiDecimal processTotalAmount;
    private KualiInteger processTotalCount;
    
    private DisbursementType disbursementType;
    private PaymentProcess process;
    private CustomerProfile customer;
    
    public ProcessSummary() {
    }

    public KualiInteger getBeginDisbursementNbr() {
        return beginDisbursementNbr;
    }

    public void setBeginDisbursementNbr(KualiInteger beginDisbursementNbr) {
        this.beginDisbursementNbr = beginDisbursementNbr;
    }

    public CustomerProfile getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerProfile customer) {
        this.customer = customer;
    }

    public DisbursementType getDisbursementType() {
        return disbursementType;
    }

    public void setDisbursementType(DisbursementType disbursementType) {
        this.disbursementType = disbursementType;
    }

    public KualiInteger getEndDisbursementNbr() {
        return endDisbursementNbr;
    }

    public void setEndDisbursementNbr(KualiInteger endDisbursementNbr) {
        this.endDisbursementNbr = endDisbursementNbr;
    }

    public KualiInteger getId() {
        return id;
    }

    public void setId(KualiInteger id) {
        this.id = id;
    }

    public PaymentProcess getProcess() {
        return process;
    }

    public void setProcess(PaymentProcess process) {
        this.process = process;
    }

    public KualiDecimal getProcessTotalAmount() {
        return processTotalAmount;
    }

    public void setProcessTotalAmount(KualiDecimal processTotalAmount) {
        this.processTotalAmount = processTotalAmount;
    }

    public KualiInteger getProcessTotalCount() {
        return processTotalCount;
    }

    public void setProcessTotalCount(KualiInteger processTotalCount) {
        this.processTotalCount = processTotalCount;
    }
    
    public KualiInteger getCustomerId() {
        return customerId;
    }

    public void setCustomerId(KualiInteger customerId) {
        this.customerId = customerId;
    }

    public String getDisbursementTypeCode() {
        return disbursementTypeCode;
    }

    public void setDisbursementTypeCode(String disbursementTypeCode) {
        this.disbursementTypeCode = disbursementTypeCode;
    }

    public KualiInteger getProcessId() {
        return processId;
    }

    public void setProcessId(KualiInteger processId) {
        this.processId = processId;
    }
    
    public String getSortGroupName(){
       PaymentGroupService paymentGroupService = SpringContext.getBean(PaymentGroupService.class);
       String sortGroupName = paymentGroupService.getSortGroupName(sortGroupId.intValue());
       return sortGroupName;
    }
    
    public void setSortGroupName(){
        
    }
    
      /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        
        m.put(KFSPropertyConstants.ID, this.id);

        return m;
    }

    public KualiInteger getSortGroupId() {
        return sortGroupId;
    }

    public void setSortGroupId(KualiInteger sortGroupId) {
        this.sortGroupId = sortGroupId;
    }

}
