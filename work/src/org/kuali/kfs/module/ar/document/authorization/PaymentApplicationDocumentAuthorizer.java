/*
 * Copyright 2007-2008 The Kuali Foundation
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

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader;
import org.kuali.kfs.module.ar.document.PaymentApplicationDocument;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentAuthorizerBase;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Document Authorizer for the Payment Application Document
 */
public class PaymentApplicationDocumentAuthorizer extends FinancialSystemTransactionalDocumentAuthorizerBase {

    /**
     * Adding the role qualifications for the processing chart and organization
     * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizerBase#addRoleQualification(org.kuali.rice.kns.bo.BusinessObject, java.util.Map)
     */
    @Override
    protected void addRoleQualification(Object businessObject, Map<String, String> attributes) {
        super.addRoleQualification(businessObject, attributes);
        if (businessObject != null && businessObject instanceof PaymentApplicationDocument) {
            final PaymentApplicationDocument document = (PaymentApplicationDocument)businessObject;
            final WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
            if (workflowDocument.isInitiated() || workflowDocument.isSaved() || workflowDocument.isCompletionRequested()) { // only add processing chart and org if we're PreRoute
                final AccountsReceivableDocumentHeader arDocumentHeader = document.getAccountsReceivableDocumentHeader();
                if (!ObjectUtils.isNull(arDocumentHeader)) {
                    if (!StringUtils.isBlank(arDocumentHeader.getProcessingChartOfAccCodeAndOrgCode())) {
                        attributes.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, arDocumentHeader.getProcessingChartOfAccountCode());
                    }
                    if (!StringUtils.isBlank(arDocumentHeader.getProcessingOrganizationCode())) {
                        attributes.put(KfsKimAttributes.ORGANIZATION_CODE, arDocumentHeader.getProcessingOrganizationCode());
                    }
                }
            }
        }
    }

}

