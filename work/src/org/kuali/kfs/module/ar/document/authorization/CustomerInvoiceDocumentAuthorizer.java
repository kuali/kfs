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
package org.kuali.kfs.module.ar.document.authorization;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.AccountingDocumentAuthorizerBase;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.krad.util.GlobalVariables;

public class CustomerInvoiceDocumentAuthorizer extends AccountingDocumentAuthorizerBase {

    @Override
    protected void addRoleQualification(Object businessObject, Map<String, String> attributes) {
        super.addRoleQualification(businessObject, attributes);

        CustomerInvoiceDocument document = (CustomerInvoiceDocument) businessObject;
        CustomerInvoiceDocumentService invoiceDocService = SpringContext.getBean(CustomerInvoiceDocumentService.class);
        Collection<CustomerInvoiceDetail> invoiceDetails = invoiceDocService.getCustomerInvoiceDetailsForCustomerInvoiceDocumentWithCaching(document);

        for (CustomerInvoiceDetail invoiceDetail : invoiceDetails) {
            if (StringUtils.isNotBlank(invoiceDetail.getChartOfAccountsCode()) && StringUtils.isNotBlank(invoiceDetail.getAccountNumber())) {
                attributes.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, invoiceDetail.getChartOfAccountsCode());
                attributes.put(KfsKimAttributes.ACCOUNT_NUMBER, invoiceDetail.getAccountNumber());
            }
        }

        // add profile principal id - hopefully someday, TEM can simply do this for us if it is turned on
        final String currentUserPrincipalId = GlobalVariables.getUserSession().getPrincipalId();
        attributes.put(KfsKimAttributes.PROFILE_PRINCIPAL_ID, currentUserPrincipalId);
    }

}
