/*
 * Copyright 2008 The Kuali Foundation
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
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;

public class ReceivableCustomerInvoiceDetail extends CustomerInvoiceDetail {

   private CustomerInvoiceDetail postable;
   private CustomerInvoiceDocument poster;
   private boolean isUsingReceivableFAU;
   
   public ReceivableCustomerInvoiceDetail(CustomerInvoiceDetail postable, CustomerInvoiceDocument poster){
       
       this.poster = poster;
       this.postable = postable;
       
       String receivableOffsetOption = SpringContext.getBean(ParameterService.class).getParameterValueAsString(CustomerInvoiceDocument.class, ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD);
       isUsingReceivableFAU = ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD_FAU.equals( receivableOffsetOption ); 
       if( isUsingReceivableFAU ){
           this.poster.refreshReferenceObject("paymentAccount");
           this.poster.refreshReferenceObject("paymentChartOfAccounts");
           this.poster.refreshReferenceObject("paymentSubAccount");
           this.poster.refreshReferenceObject("paymentFinancialObject");
           this.poster.refreshReferenceObject("paymentFinancialSubObject");
           this.poster.refreshReferenceObject("paymentProject");           
       } else {
           this.postable.refreshNonUpdateableReferences();
       }
       
   }

   @Override
    public Account getAccount() {
        if ( isUsingReceivableFAU ){
            return poster.getPaymentAccount();
        } else {
            return postable.getAccount();
        }
    }
   
   @Override
    public String getAccountNumber() {
        if ( isUsingReceivableFAU ){
            return poster.getPaymentAccountNumber();
        } else {
            return postable.getAccountNumber();
        }
    }

   @Override
    public KualiDecimal getAmount() {
        return postable.getInvoiceItemPreTaxAmount();
    }

   @Override
    public String getChartOfAccountsCode() {
        if ( isUsingReceivableFAU ){
            return poster.getPaymentChartOfAccountsCode();
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
            return poster.getPaymentFinancialObjectCode();
        } else {
            return postable.getAccountsReceivableObjectCode();
        }   
    }

   @Override
    public ObjectCode getObjectCode() {
       if ( isUsingReceivableFAU ){
           return poster.getPaymentFinancialObject();
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
            return poster.getPaymentOrganizationReferenceIdentifier();
        } else {
            return postable.getOrganizationReferenceId();
        }
    }

   @Override
    public String getProjectCode() {
        if ( isUsingReceivableFAU ){
            return poster.getPaymentProjectCode();
        } else {
            return postable.getProjectCode();
        }
    }

   @Override
    public String getSubAccountNumber() {
        if ( isUsingReceivableFAU ){
            return poster.getPaymentSubAccountNumber();
        } else {
            return postable.getSubAccountNumber();
        }
    }

}
