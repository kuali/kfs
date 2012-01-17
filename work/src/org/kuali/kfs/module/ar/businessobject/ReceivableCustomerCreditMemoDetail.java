/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.kfs.module.ar.businessobject;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.document.CustomerCreditMemoDocument;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;

public class ReceivableCustomerCreditMemoDetail extends CustomerCreditMemoDetail {
    private CustomerCreditMemoDetail postable;
    private CustomerCreditMemoDocument poster;
    private boolean isUsingReceivableFAU;
    
    public ReceivableCustomerCreditMemoDetail(CustomerCreditMemoDetail postable, CustomerCreditMemoDocument poster){
        
        this.poster = poster;
        this.postable = postable;
        
        String receivableOffsetOption = SpringContext.getBean(ParameterService.class).getParameterValueAsString(CustomerInvoiceDocument.class, ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD);
        isUsingReceivableFAU = ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD_FAU.equals( receivableOffsetOption ); 
        if( isUsingReceivableFAU ){
            this.poster.getInvoice().refreshReferenceObject("paymentAccount");
            this.poster.getInvoice().refreshReferenceObject("paymentChartOfAccounts");
            this.poster.getInvoice().refreshReferenceObject("paymentSubAccount");
            this.poster.getInvoice().refreshReferenceObject("paymentFinancialObject");
            this.poster.getInvoice().refreshReferenceObject("paymentFinancialSubObject");
            this.poster.getInvoice().refreshReferenceObject("paymentProject");           
        } else {
            this.postable.refreshNonUpdateableReferences();
        }
        
    }

    @Override
     public Account getAccount() {
         if ( isUsingReceivableFAU ){
             return poster.getInvoice().getPaymentAccount();
         } else {
             return postable.getAccount();
         }
     }
    
    @Override
     public String getAccountNumber() {
         if ( isUsingReceivableFAU ){
             return poster.getInvoice().getPaymentAccountNumber();
         } else {
             return postable.getAccountNumber();
         }
     }

    @Override
     public KualiDecimal getAmount() {
         return postable.getCreditMemoItemTotalAmount();
     }

    @Override
     public String getChartOfAccountsCode() {
         if ( isUsingReceivableFAU ){
             return poster.getInvoice().getPaymentChartOfAccountsCode();
         } else {
             return postable.getChartOfAccountsCode();
         }        
     }

    @Override
     public String getDocumentNumber() {
         return postable.getDocumentNumber();
     }

    @Override
     public String getFinancialDocumentLineDescription() {
         return postable.getFinancialDocumentLineDescription();
     }

    @Override
     public String getFinancialObjectCode() {
         if ( isUsingReceivableFAU ){
             return poster.getInvoice().getPaymentFinancialObjectCode();
         } else {
             return postable.getAccountsReceivableObjectCode();
         }   
     }

    @Override
     public ObjectCode getObjectCode() {
        if ( isUsingReceivableFAU ){
            return poster.getInvoice().getPaymentFinancialObject();
        } else {
            return postable.getAccountsReceivableObject();
        }
     }
    
    @Override
    public String getFinancialSubObjectCode() {
        return GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankFinancialSubObjectCode(); 
    }

    @Override
     public String getOrganizationReferenceId() {
         if ( isUsingReceivableFAU ){
             return poster.getInvoice().getPaymentOrganizationReferenceIdentifier();
         } else {
             return postable.getOrganizationReferenceId();
         }
     }

    @Override
     public String getProjectCode() {
         if ( isUsingReceivableFAU ){
             return poster.getInvoice().getPaymentProjectCode();
         } else {
             return postable.getProjectCode();
         }
     }

    @Override
     public String getSubAccountNumber() {
         if ( isUsingReceivableFAU ){
             return poster.getInvoice().getPaymentSubAccountNumber();
         } else {
             return postable.getSubAccountNumber();
         }
     }

}
