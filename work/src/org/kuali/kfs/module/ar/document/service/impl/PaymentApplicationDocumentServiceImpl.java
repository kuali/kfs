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
package org.kuali.kfs.module.ar.document.service.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader;
import org.kuali.kfs.module.ar.businessobject.CashControlDetail;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.InvoicePaidApplied;
import org.kuali.kfs.module.ar.businessobject.NonAppliedHolding;
import org.kuali.kfs.module.ar.document.CashControlDocument;
import org.kuali.kfs.module.ar.document.PaymentApplicationDocument;
import org.kuali.kfs.module.ar.document.service.AccountsReceivableDocumentHeaderService;
import org.kuali.kfs.module.ar.document.service.NonAppliedHoldingService;
import org.kuali.kfs.module.ar.document.service.PaymentApplicationDocumentService;
import org.kuali.kfs.sys.context.SpringContext;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class PaymentApplicationDocumentServiceImpl implements PaymentApplicationDocumentService {
    private BusinessObjectService businessObjectService;
    private NonAppliedHoldingService nonAppliedHoldingService;

    public CashControlDocument getCashControlDocumentForPaymentApplicationDocument(PaymentApplicationDocument document) {
        return getCashControlDocumentForPaymentApplicationDocumentNumber(document.getDocumentNumber());
    }

    public KualiDecimal getTotalAppliedAmountForPaymentApplicationDocument(PaymentApplicationDocument document) {
        KualiDecimal total = KualiDecimal.ZERO;
        Collection<InvoicePaidApplied> invoicePaidApplieds = document.getAppliedPayments();

        for (InvoicePaidApplied invoicePaidApplied : invoicePaidApplieds) {
            total = total.add(invoicePaidApplied.getInvoiceItemAppliedAmount());
        }

        return total;
    }

    public KualiDecimal getTotalToBeAppliedForPaymentApplicationDocument(String paymentApplicationDocumentNumber) {
        KualiDecimal total = KualiDecimal.ZERO;

        // TODO Auto-generated method stub
        // for test purpose only
        total = total.add(new KualiDecimal(1100));

        return total;
    }

    public KualiDecimal getTotalUnappliedFundsForPaymentApplicationDocument(PaymentApplicationDocument document) {
        KualiDecimal total = KualiDecimal.ZERO;

        String customerNumber = document.getAccountsReceivableDocumentHeader().getCustomerNumber();
        Collection<NonAppliedHolding> nonAppliedHoldings = nonAppliedHoldingService.getNonAppliedHoldingsForCustomer(customerNumber);

        for (NonAppliedHolding nonAppliedHolding : nonAppliedHoldings) {
            total = total.add(nonAppliedHolding.getFinancialDocumentLineAmount());
        }

        return total;
    }

    public KualiDecimal getTotalUnappliedFundsToBeAppliedForPaymentApplicationDocument(PaymentApplicationDocument document) {
        KualiDecimal totalUnapplied = getTotalUnappliedFundsForPaymentApplicationDocument(document);
        KualiDecimal totalApplied = getTotalAppliedAmountForPaymentApplicationDocument(document);
        return totalUnapplied.subtract(totalApplied);
    }

    @SuppressWarnings("unchecked")
    public CashControlDocument getCashControlDocumentForPaymentApplicationDocumentNumber(String paymentApplicationDocumentNumber) {
        if (null == paymentApplicationDocumentNumber) {
            return null;
        }
        CashControlDocument document = null;
        Map criteria = new HashMap();
        criteria.put("referenceFinancialDocumentNumber", paymentApplicationDocumentNumber);

        Collection matches = businessObjectService.findMatching(CashControlDetail.class, criteria);
        if (matches.size() > 0) {
            CashControlDetail detail = (CashControlDetail) matches.iterator().next();
            Map ccdocCriteria = new HashMap();
            ccdocCriteria.put("documentNumber", detail.getDocumentNumber());
            Object ccdoc = businessObjectService.findByPrimaryKey(CashControlDocument.class, ccdocCriteria);
            document = (CashControlDocument) ccdoc;
        }
        return document;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }


    /**
     * @see org.kuali.kfs.module.ar.document.service.PaymentApplicationDocumentService#createInvoicePaidAppliedForInvoiceDetail(org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail,
     *      org.kuali.core.util.KualiDecimal)
     */
    public InvoicePaidApplied createInvoicePaidAppliedForInvoiceDetail(CustomerInvoiceDetail customerInvoiceDetail, String applicationDocNbr, Integer universityFiscalYear, String universityFiscalPeriodCode, KualiDecimal amount, Integer invoicePaidAppliedItemNbr) {

        Collection<InvoicePaidApplied> invoicePaidApplieds = customerInvoiceDetail.getInvoicePaidApplieds();
        InvoicePaidApplied invoicePaidApplied = null;
        boolean found = false;

        if (invoicePaidApplieds != null && invoicePaidApplieds.size() > 0) {
            for (InvoicePaidApplied pdApp : invoicePaidApplieds) {
                if (pdApp.getDocumentNumber().equals(applicationDocNbr) && pdApp.getFinancialDocumentReferenceInvoiceNumber().equals(customerInvoiceDetail.getDocumentNumber()) && pdApp.getInvoiceItemNumber().equals(customerInvoiceDetail.getSequenceNumber())) {
                    pdApp.setInvoiceItemAppliedAmount(amount);
                    found = true;
                    break;
                }
            }
        }

        if (!found) {

            invoicePaidApplied = new InvoicePaidApplied();

            invoicePaidApplied.setDocumentNumber(applicationDocNbr);
            
            invoicePaidApplied.setFinancialDocumentReferenceInvoiceNumber(customerInvoiceDetail.getDocumentNumber());
            invoicePaidApplied.setInvoiceItemNumber(customerInvoiceDetail.getSequenceNumber());
            invoicePaidApplied.setInvoiceItemAppliedAmount(amount);
            invoicePaidApplied.setUniversityFiscalYear(universityFiscalYear);
            invoicePaidApplied.setUniversityFiscalPeriodCode(universityFiscalPeriodCode);
            invoicePaidApplied.setPaidAppliedItemNumber(invoicePaidAppliedItemNbr);

        }

        return invoicePaidApplied;
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public NonAppliedHoldingService getNonAppliedHoldingService() {
        return nonAppliedHoldingService;
    }

    public void setNonAppliedHoldingService(NonAppliedHoldingService nonAppliedHoldingService) {
        this.nonAppliedHoldingService = nonAppliedHoldingService;
    }
}
