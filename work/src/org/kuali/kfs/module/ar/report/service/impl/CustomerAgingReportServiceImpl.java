/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.report.service.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.ar.businessobject.CustomerAgingReportDetail;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDetailService;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService;
import org.kuali.kfs.module.ar.report.service.CustomerAgingReportService;
import org.kuali.kfs.module.ar.report.util.CustomerAgingReportDataHolder;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * This class...
 */
public class CustomerAgingReportServiceImpl implements CustomerAgingReportService {


    private DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

    private DateTimeService dateTimeService;


    /**
     * @see org.kuali.kfs.module.ar.report.service.CustomerAgingReportService#calculateAgingReportAmounts()
     */
    public CustomerAgingReportDataHolder calculateAgingReportAmounts(Collection<CustomerInvoiceDetail> details, Date reportRunDate) {
        CustomerAgingReportDataHolder agingData = new CustomerAgingReportDataHolder();
        
        KualiDecimal total0to30 = KualiDecimal.ZERO;
        KualiDecimal total31to60 = KualiDecimal.ZERO;
        KualiDecimal total61to90 = KualiDecimal.ZERO;
        KualiDecimal total91toSYSPR = KualiDecimal.ZERO;
        KualiDecimal totalSYSPRplus1orMore = KualiDecimal.ZERO;

        String nbrDaysForLastBucket = SpringContext.getBean(ParameterService.class).getParameterValue(CustomerAgingReportDetail.class, "CUSTOMER_INVOICE_AGE");     // ArConstants.CUSTOMER_INVOICE_AGE); // default is 120

        Date cutoffdate30 = DateUtils.addDays(reportRunDate, -30);
        Date cutoffdate60 = DateUtils.addDays(reportRunDate, -60);
        Date cutoffdate90 = DateUtils.addDays(reportRunDate, -90);
        Date cutoffdate120 = DateUtils.addDays(reportRunDate, -1*Integer.parseInt(nbrDaysForLastBucket));
        
        Map<String, Object> knownCustomers = new HashMap<String, Object>(details.size());

        CustomerInvoiceDetailService customerInvoiceDetailService = SpringContext.getBean(CustomerInvoiceDetailService.class);

        KualiDecimal totalBalance = new KualiDecimal(0.00);
        CustomerAgingReportDetail custDetail = null;
        String previousCustomerNumber = "";
        int detailnum=0;
        // iterate over all invoices consolidating balances for each customer
        for (CustomerInvoiceDetail cid : details) {
            String invoiceDocumentNumber = cid.getDocumentNumber();
            CustomerInvoiceDocument custInvoice = SpringContext.getBean(CustomerInvoiceDocumentService.class).getInvoiceByInvoiceDocumentNumber(invoiceDocumentNumber);
            Date approvalDate;
            detailnum++;
            approvalDate=custInvoice.getBillingDate(); // use this if above isn't set since this is never null
            if (ObjectUtils.isNull(approvalDate)) {
                continue;
            }

            if(custInvoice!=null && cid.getAmountOpenFromDatabase().isNonZero()) {

                Customer customerobj = custInvoice.getCustomer();                        
                String customerNumber = customerobj.getCustomerNumber();    // tested and works
                String customerName = customerobj.getCustomerName();  // tested and works

                if (knownCustomers.containsKey(customerNumber)) { 
                    custDetail = (CustomerAgingReportDetail) knownCustomers.get(customerNumber);
                } else {
                    custDetail = new CustomerAgingReportDetail();
                    custDetail.setCustomerName(customerName);
                    custDetail.setCustomerNumber(customerNumber);
                    knownCustomers.put(customerNumber, custDetail);
                }
                KualiDecimal amountOpenOnReportRunDate = cid.getAmountOpenByDateFromDatabase(reportRunDate);
                if (!approvalDate.after(reportRunDate) && !approvalDate.before(cutoffdate30)) {                                
                    custDetail.setUnpaidBalance0to30(amountOpenOnReportRunDate.add(custDetail.getUnpaidBalance0to30()));
                    total0to30 = total0to30.add(amountOpenOnReportRunDate);
                }
                else if (approvalDate.before(cutoffdate30) && !approvalDate.before(cutoffdate60)) {               
                    custDetail.setUnpaidBalance31to60(amountOpenOnReportRunDate.add(custDetail.getUnpaidBalance31to60()));
                    total31to60 = total31to60.add(amountOpenOnReportRunDate);
                }
                else if (approvalDate.before(cutoffdate60) && !approvalDate.before(cutoffdate90)) {
                    custDetail.setUnpaidBalance61to90(amountOpenOnReportRunDate.add(custDetail.getUnpaidBalance61to90()));
                    total61to90 = total61to90.add(amountOpenOnReportRunDate);
                }
                else if (approvalDate.before(cutoffdate90) && !approvalDate.before(cutoffdate120)) {
                    custDetail.setUnpaidBalance91toSYSPR(amountOpenOnReportRunDate.add(custDetail.getUnpaidBalance91toSYSPR()));
                    total91toSYSPR = total91toSYSPR.add(amountOpenOnReportRunDate);
                }
                else if (approvalDate.before(cutoffdate120)) {
                    custDetail.setUnpaidBalanceSYSPRplus1orMore(amountOpenOnReportRunDate.add(custDetail.getUnpaidBalanceSYSPRplus1orMore()));
                    totalSYSPRplus1orMore = totalSYSPRplus1orMore.add(amountOpenOnReportRunDate);
                }            
                totalBalance = totalBalance.add(amountOpenOnReportRunDate);
            }        
        } 
        
        agingData.setTotal0to30(total0to30);
        agingData.setTotal31to60(total31to60);
        agingData.setTotal61to90(total61to90);
        agingData.setTotal91toSYSPR(total91toSYSPR);
        agingData.setTotalSYSPRplus1orMore(totalSYSPRplus1orMore);
        agingData.setTotalAmountDue(totalBalance);
        
        agingData.setKnownCustomers(knownCustomers);
        
        return agingData;
    }

    /**
     * 
     * This method...
     * @return
     */
    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    /**
     * 
     * This method...
     * @param dateTimeService
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }
    
}
