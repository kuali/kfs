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
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public class ReceivableCustomerInvoiceDetail extends CustomerInvoiceDetail {

   private CustomerInvoiceDetail postable;
   private CustomerInvoiceDocument poster;
   private boolean isUsingReceivableFAU;

   public ReceivableCustomerInvoiceDetail(CustomerInvoiceDetail postable, CustomerInvoiceDocument poster){
       this.poster = poster;
       this.postable = postable;
       this.postable.refreshNonUpdateableReferences();
   }

   @Override
    public Account getAccount() {
       return postable.getAccount();
    }

   @Override
    public String getAccountNumber() {
       return postable.getAccountNumber();
    }

   @Override
    public KualiDecimal getAmount() {
        return postable.getInvoiceItemPreTaxAmount();
    }

   @Override
    public String getChartOfAccountsCode() {
       return postable.getChartOfAccountsCode();
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
       return postable.getAccountsReceivableObjectCode();
    }

   @Override
    public ObjectCode getObjectCode() {
       return postable.getAccountsReceivableObject();
    }

   @Override
   public String getFinancialSubObjectCode() {
       return GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankFinancialSubObjectCode();
   }

   @Override
    public String getOrganizationReferenceId() {
       return postable.getOrganizationReferenceId();
    }

   @Override
    public String getProjectCode() {
       return postable.getProjectCode();
    }

   @Override
    public String getSubAccountNumber() {
       return postable.getSubAccountNumber();
    }

}
