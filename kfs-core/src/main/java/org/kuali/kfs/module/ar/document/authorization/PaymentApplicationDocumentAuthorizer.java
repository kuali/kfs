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

