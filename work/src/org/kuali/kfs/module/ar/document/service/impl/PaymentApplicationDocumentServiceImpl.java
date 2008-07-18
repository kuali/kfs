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
import org.kuali.kfs.module.ar.businessobject.CashControlDetail;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.InvoicePaidApplied;
import org.kuali.kfs.module.ar.document.CashControlDocument;
import org.kuali.kfs.module.ar.document.PaymentApplicationDocument;
import org.kuali.kfs.module.ar.document.service.PaymentApplicationDocumentService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class PaymentApplicationDocumentServiceImpl implements PaymentApplicationDocumentService {
    private BusinessObjectService businessObjectService;

    public CashControlDocument getCashControlDocumentForPaymentApplicationDocument(PaymentApplicationDocument document) {
        return getCashControlDocumentForPaymentApplicationDocumentNumber(document.getDocumentNumber());
    }

    public KualiDecimal getTotalAppliedAmountForPaymentApplicationDocument(String paymentApplicationDocumentNumber) {
        KualiDecimal total = new KualiDecimal(0);

        // TODO Auto-generated method stub

        return total;
    }

    public KualiDecimal getTotalCashControlForPaymentApplicationDocument(String paymentApplicationDocumentNumber) {
        KualiDecimal total = new KualiDecimal(0);
        CashControlDocument ccd = getCashControlDocumentForPaymentApplicationDocumentNumber(paymentApplicationDocumentNumber);
        if (null != ccd && null != ccd.getCashControlTotalAmount()) {
            total = total.add(ccd.getCashControlTotalAmount());
        }
        return total;
    }

    public KualiDecimal getTotalToBeAppliedForPaymentApplicationDocument(String paymentApplicationDocumentNumber) {
        KualiDecimal total = new KualiDecimal(0);

        // TODO Auto-generated method stub

        return total;
    }

    public KualiDecimal getTotalUnappliedFundsForPaymentApplicationDocument(String paymentApplicationDocumentNumber) {
        KualiDecimal total = new KualiDecimal(0);

        // TODO Auto-generated method stub

        return total;
    }

    public KualiDecimal getTotalUnappliedFundsToBeAppliedForPaymentApplicationDocument(String paymentApplicationDocumentNumber) {
        KualiDecimal total = new KualiDecimal(0);

        // TODO Auto-generated method stub

        return total;
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
    public InvoicePaidApplied createInvoicePaidAppliedForInvoiceDetail(CustomerInvoiceDetail customerInvoiceDetail, String applicationDocNbr, Integer universityFiscalYear, String universityFiscalPeriodCode, KualiDecimal amount) {

        Collection<InvoicePaidApplied> invoicePaidApplieds = customerInvoiceDetail.getInvoicePaidApplieds();
        InvoicePaidApplied invoicePaidApplied = null;
        Integer invoicePaidAppliedItemNbr = 1;
        boolean found = false;

        if (invoicePaidApplieds != null || invoicePaidApplieds.size() > 0) {
            for (InvoicePaidApplied pdApp : invoicePaidApplieds) {
                if (pdApp.getDocumentNumber().equals(applicationDocNbr) && pdApp.getFinancialDocumentReferenceInvoiceNumber().equals(customerInvoiceDetail.getDocumentNumber()) && pdApp.getInvoiceItemNumber().equals(customerInvoiceDetail.getSequenceNumber())) {
                    pdApp.setInvoiceItemAppliedAmount(amount);
                    found = true;
                    break;
                }
            }
            if (!found) {
                invoicePaidAppliedItemNbr = invoicePaidApplieds.size();
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
}
