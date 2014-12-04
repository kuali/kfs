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

