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
import org.kuali.kfs.module.ar.document.CustomerInvoiceWriteoffDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;


public class WriteoffTaxCustomerInvoiceDetail extends CustomerInvoiceDetail {
    
    private CustomerInvoiceDetail postable;
    private CustomerInvoiceWriteoffDocument poster;
    private boolean isUsingTaxLiabilityAdjustmentIndNo;
    
    public WriteoffTaxCustomerInvoiceDetail(CustomerInvoiceDetail postable, CustomerInvoiceWriteoffDocument poster){
        this.postable = postable;
        this.poster = poster;
        
        String writeoffTaxGenerationOption = SpringContext.getBean(ParameterService.class).getParameterValueAsString(CustomerInvoiceWriteoffDocument.class, ArConstants.ALLOW_SALES_TAX_LIABILITY_ADJUSTMENT_IND);
        isUsingTaxLiabilityAdjustmentIndNo = ArConstants.ALLOW_SALES_TAX_LIABILITY_ADJUSTMENT_IND_NO.equals( writeoffTaxGenerationOption );

        if( isUsingTaxLiabilityAdjustmentIndNo ){
            //if is using org account default, I already set the writeoff FAU on
            //the document, so that is needed to do is refresh the FAU objects
            this.poster.refreshReferenceObject("account");
            this.poster.refreshReferenceObject("chartOfAccounts");
            this.poster.refreshReferenceObject("subAccount");
            this.poster.refreshReferenceObject("financialObject");
            this.poster.refreshReferenceObject("financialSubObject");
            this.poster.refreshReferenceObject("project");                      
        } else {
            this.postable.refreshNonUpdateableReferences();
        }        
    }
    
    @Override
    public Account getAccount() {
        if ( isUsingTaxLiabilityAdjustmentIndNo ){
            return poster.getAccount();
        } else {
            return postable.getAccount();
        }
    }
   
   @Override
    public String getAccountNumber() {
        if ( isUsingTaxLiabilityAdjustmentIndNo ){
            return poster.getAccountNumber();
        } else {
            return postable.getAccountNumber();
        }
    }

   @Override
    public KualiDecimal getAmount() {
        return postable.getAmountOpen();
    }

   @Override
    public String getChartOfAccountsCode() {
        if ( isUsingTaxLiabilityAdjustmentIndNo ){
            return poster.getChartOfAccountsCode();
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
       if ( isUsingTaxLiabilityAdjustmentIndNo ){
           return poster.getFinancialObjectCode();
       } else {
           return postable.getAccountsReceivableObjectCode();
       }   
   }

  @Override
   public ObjectCode getObjectCode() {
      if ( isUsingTaxLiabilityAdjustmentIndNo ){
          return poster.getFinancialObject();
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
        if ( isUsingTaxLiabilityAdjustmentIndNo ){
            return poster.getOrganizationReferenceIdentifier();
        } else {
            return postable.getOrganizationReferenceId();
        }
    }

   @Override
    public String getProjectCode() {
        if ( isUsingTaxLiabilityAdjustmentIndNo ){
            return poster.getProjectCode();
        } else {
            return postable.getProjectCode();
        }
    }

   @Override
    public String getSubAccountNumber() {
        if ( isUsingTaxLiabilityAdjustmentIndNo ){
            return poster.getSubAccountNumber();
        } else {
            return postable.getSubAccountNumber();
        }
    }
}
