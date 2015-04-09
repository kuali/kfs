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
package org.kuali.kfs.fp.document.authorization;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.document.DistributionOfIncomeAndExpenseDocument;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.KfsAuthorizationConstants;
import org.kuali.kfs.sys.KFSConstants.RouteLevelNames;
import org.kuali.kfs.sys.businessobject.ElectronicPaymentClaim;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.document.authorization.AccountingDocumentPresentationControllerBase;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.KRADConstants;

public class DistributionOfIncomeAndExpenseDocumentPresentationController extends AccountingDocumentPresentationControllerBase {

    /**
     * @see org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationControllerBase#getEditModes(org.kuali.rice.krad.document.Document)
     */
    @Override
    public Set<String> getEditModes(Document document) {
        Set<String> editModes = super.getEditModes(document);

        DistributionOfIncomeAndExpenseDocument distributionOfIncomeAndExpenseDocument = (DistributionOfIncomeAndExpenseDocument) document;
        
        List<ElectronicPaymentClaim> electronicPaymentClaims = distributionOfIncomeAndExpenseDocument.getElectronicPaymentClaims();

        if (electronicPaymentClaims == null) {
            distributionOfIncomeAndExpenseDocument.refreshReferenceObject(KFSPropertyConstants.ELECTRONIC_PAYMENT_CLAIMS);
            electronicPaymentClaims = distributionOfIncomeAndExpenseDocument.getElectronicPaymentClaims();
        }

        if (electronicPaymentClaims != null && electronicPaymentClaims.size() > 0) {
            editModes.add(KfsAuthorizationConstants.DistributionOfIncomeAndExpenseEditMode.SOURCE_LINE_READ_ONLY_MODE);
        }

        return editModes;
    }
    
    /**
     * @see org.kuali.rice.krad.document.authorization.DocumentPresentationControllerBase#getDocumentActions(org.kuali.rice.krad.document.Document)
     */
    @Override
    public Set<String> getDocumentActions(Document document) {
        Set<String> documentActions = super.getDocumentActions(document);

        DistributionOfIncomeAndExpenseDocument distributionOfIncomeAndExpenseDocument = (DistributionOfIncomeAndExpenseDocument) document;
        String docInError = distributionOfIncomeAndExpenseDocument.getFinancialSystemDocumentHeader().getFinancialDocumentInErrorNumber();
        
        if (StringUtils.isNotBlank(docInError)) {
            documentActions.add(KRADConstants.KUALI_ACTION_CAN_EDIT);
        }
        return documentActions;
    }
    
    /**
     * @see org.kuali.rice.krad.document.authorization.DocumentPresentationControllerBase#canEdit(org.kuali.rice.krad.document.Document)
     */
    @Override
    public boolean canEdit(Document document) {
        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        FinancialSystemDocumentHeader documentheader = (FinancialSystemDocumentHeader) (document.getDocumentHeader());

        if (workflowDocument.isCanceled()) {
            return false;
        }
        else if (workflowDocument.isEnroute()) {
            Set<String> currentRouteLevels = workflowDocument.getCurrentNodeNames();

            if (currentRouteLevels.contains(RouteLevelNames.ACCOUNTING_ORGANIZATION_HIERARCHY)) {
                return false;
            }
        }

        return super.canEdit(document);
    }
}
