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
package org.kuali.kfs.module.ar.document.validation.impl;

import org.kuali.core.document.Document;
import org.kuali.core.rules.TransactionalDocumentRuleBase;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.document.CustomerInvoiceWriteoffDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.rice.kns.util.KNSConstants;

public class CustomerInvoiceWriteoffDocumentRule extends TransactionalDocumentRuleBase {
    
    
    /**
     * @see org.kuali.core.rules.DocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.core.document.Document)
     */
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        boolean isValid = super.processCustomSaveDocumentBusinessRules(document);
        
        GlobalVariables.getErrorMap().addToErrorPath(KNSConstants.DOCUMENT_PROPERTY_NAME);
        
        CustomerInvoiceWriteoffDocument customerCreditMemoDocument = (CustomerInvoiceWriteoffDocument)document;
        
        String writeoffGenerationOption = SpringContext.getBean(ParameterService.class).getParameterValue(CustomerInvoiceWriteoffDocument.class, ArConstants.GLPE_WRITEOFF_GENERATION_METHOD);
        boolean isUsingChartForWriteoff = ArConstants.GLPE_WRITEOFF_GENERATION_METHOD_CHART.equals( writeoffGenerationOption );
        
        if( isUsingChartForWriteoff ){
            for( CustomerInvoiceDetail customerInvoiceDetail : customerCreditMemoDocument.getCustomerInvoiceDocument().getCustomerInvoiceDetailsWithoutDiscounts())
            isValid &= doesChartCodeHaveCorrespondingWriteoffObjectCode(customerInvoiceDetail);    
        }
        
        GlobalVariables.getErrorMap().removeFromErrorPath(KNSConstants.DOCUMENT_PROPERTY_NAME);

        return isValid;
    }
    
    /**
     * This method checks if the chart object code using on the invoice detail has a corresponding 
     * @param customerInvoiceDetail
     * @return
     * 
     * TODO
     */
    protected boolean doesChartCodeHaveCorrespondingWriteoffObjectCode(CustomerInvoiceDetail customerInvoiceDetail){
        boolean isValid = true;
        
        /*
        customerInvoiceDetail.refreshReferenceObject("chart");
        if (ObjectUtils.isNotNull(customerInvoiceDetail.getChart())) {
            if (ObjectUtils.isNotNull(customerInvoiceDetail.getChart().getFinAccountsReceivableObjCode())) {
                GlobalVariables.getErrorMap().putError("", ArConstants.ERROR_CUSTOMER_INVOICE_WRITEOFF_CHART_WRITEOFF_OBJECT_DOESNT_EXIST);
                isValid = false;
            }
        }
        else {
            GlobalVariables.getErrorMap().putError("", ArConstants.ERROR_CUSTOMER_CREDIT_MEMO_DETAIL_INVALID_DATA_INPUT);
            isValid = false;
        }*/

        return isValid;
    }

}
