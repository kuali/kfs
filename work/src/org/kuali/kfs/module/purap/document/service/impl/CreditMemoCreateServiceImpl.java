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
package org.kuali.kfs.module.purap.document.service.impl;

import org.kuali.kfs.module.purap.document.service.AccountsPayableService;
import org.kuali.kfs.module.purap.document.service.CreditMemoCreateService;
import org.kuali.kfs.module.purap.document.service.CreditMemoService;
import org.kuali.kfs.module.purap.document.service.PaymentRequestService;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.module.purap.service.PurapAccountingService;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.kns.service.DataDictionaryService;


/**
 * Performs initial population of the credit memo document.
 */
public class CreditMemoCreateServiceImpl implements CreditMemoCreateService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CreditMemoServiceImpl.class);
    private VendorService vendorService;
    private CreditMemoService creditMemoService;
    private AccountsPayableService accountsPayableService;
    private PurapService purapService;
    private PurchaseOrderService purchaseOrderService;
    private PaymentRequestService paymentRequestService;
    private DataDictionaryService dataDictionaryService;
    private PurapAccountingService purapAccountingService;
    
    public void setCreditMemoService(CreditMemoService creditMemoService) {
        this.creditMemoService = creditMemoService;
    }

    public void setAccountsPayableService(AccountsPayableService accountsPayableService) {
        this.accountsPayableService = accountsPayableService;
    }

    public void setPurapService(PurapService purapService) {
        this.purapService = purapService;
    }

    public void setPurchaseOrderService(PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }

    public void setPaymentRequestService(PaymentRequestService paymentRequestService) {
        this.paymentRequestService = paymentRequestService;
    }

    public void setPurapAccountingService(PurapAccountingService purapAccountingService) {
        this.purapAccountingService = purapAccountingService;
    }
    
    
}

