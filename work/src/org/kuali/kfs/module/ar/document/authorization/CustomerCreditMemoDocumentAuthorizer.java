/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.document.authorization;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.document.CustomerCreditMemoDocument;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentAuthorizerBase;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kns.bo.BusinessObject;

public class CustomerCreditMemoDocumentAuthorizer extends FinancialSystemTransactionalDocumentAuthorizerBase {

    @Override
    protected void addRoleQualification(BusinessObject businessObject, Map<String, String> attributes) {
        super.addRoleQualification(businessObject, attributes);
        
        CustomerCreditMemoDocument creditMemoDoc = (CustomerCreditMemoDocument) businessObject;
        String invoiceDocNumber = creditMemoDoc.getFinancialDocumentReferenceInvoiceNumber();
        if (StringUtils.isBlank(invoiceDocNumber)) {
            return;
        }
        CustomerInvoiceDocumentService invoiceService = SpringContext.getBean(CustomerInvoiceDocumentService.class);
        
        Collection<CustomerInvoiceDetail> invoiceDetails = invoiceService.getCustomerInvoiceDetailsForCustomerInvoiceDocument(invoiceDocNumber);
        
        //  adds the chart/account for each account used on the original invoice that will be credited by this
        for (CustomerInvoiceDetail invoiceDetail : invoiceDetails) {
            if (StringUtils.isNotBlank(invoiceDetail.getChartOfAccountsCode()) && StringUtils.isNotBlank(invoiceDetail.getAccountNumber())) {
                attributes.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, invoiceDetail.getChartOfAccountsCode());
                attributes.put(KfsKimAttributes.ACCOUNT_NUMBER, invoiceDetail.getAccountNumber());
            }
        }
    }

}

