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
