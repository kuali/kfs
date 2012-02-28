/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.fp.document.authorization;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.document.DisbursementVoucherConstants;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.authorization.AccountingDocumentAuthorizerBase;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Adds extra role qualifiers for funky travel edit mode permission
 */
public class DisbursementVoucherDocumentAuthorizer extends AccountingDocumentAuthorizerBase {

    /**
     * Adds chart codes and account numbers for accounting lines if we're at Account level, so that the fiscal officer gets travel edit mode
     * @see org.kuali.kfs.sys.document.authorization.AccountingDocumentAuthorizerBase#addRoleQualification(org.kuali.rice.krad.bo.BusinessObject, java.util.Map)
     */
    @Override
    protected void addRoleQualification(Object dataObject, Map<String, String> attributes) {
        super.addRoleQualification(dataObject, attributes);
        final DisbursementVoucherDocument disbursementVoucherDocument = (DisbursementVoucherDocument)dataObject;

        // are we add Account level?  Then let's add our qualifiers
        if (isAtAccountLevel(disbursementVoucherDocument)) {
            addAccountQualification(getAccountingLines(disbursementVoucherDocument), attributes);
        }

        // add campus code if we have one
        if (!StringUtils.isBlank(disbursementVoucherDocument.getCampusCode())) {
            attributes.put(KimConstants.AttributeConstants.CAMPUS_CODE, disbursementVoucherDocument.getCampusCode());
        }
    }

    /**
     * Finds the source accounting lines in the given business object
     * @param disbursementVoucherDocument a document to get accounting lines from
     * @return a List of accounting lines
     */
    protected List<? extends AccountingLine> getAccountingLines(DisbursementVoucherDocument disbursementVoucherDocument) {
        return disbursementVoucherDocument.getSourceAccountingLines();
    }

    /**
     * Goes through the given List of accounting lines and fines one line where the current user is the fiscal officer; it uses that line to put chart of accounts
     * code and account number qualifications into the given Map of attributes for role qualification
     * @param accountingLines a List of AccountingLines
     * @param attributes a Map of role qualification attributes
     */
    protected void addAccountQualification(List<? extends AccountingLine> accountingLines, Map<String, String> attributes) {
        final Person currentUser = GlobalVariables.getUserSession().getPerson();
        boolean foundQualification = false;
        int count = 0;
        while (!foundQualification && count < accountingLines.size()) {
            AccountingLine accountingLine = accountingLines.get(count);
            if (ObjectUtils.isNull(accountingLine.getAccount())) {
                accountingLine.refreshReferenceObject("account");
            }
            if (!ObjectUtils.isNull(accountingLine.getAccount()) && currentUser.getPrincipalId().equalsIgnoreCase(accountingLine.getAccount().getAccountFiscalOfficerSystemIdentifier())) {
                attributes.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, accountingLine.getChartOfAccountsCode());
                attributes.put(KfsKimAttributes.ACCOUNT_NUMBER, accountingLine.getAccountNumber());
                foundQualification = true;
            }
            count += 1;
        }
    }

    /**
     * A helper method for determining the route levels for a given document.
     *
     * @param workflowDocument
     * @return List
     */
    protected Set<String> getCurrentRouteLevels(WorkflowDocument workflowDocument) {
        return workflowDocument.getCurrentNodeNames();
    }

    /**
     * Determines if the document is at the Account route level
     * @param disbursementVoucherDocument the Disbursement Voucher document to determine the account level of
     * @return true if the document is at the account level, false otherwise
     */
    protected boolean isAtAccountLevel(DisbursementVoucherDocument disbursementVoucherDocument) {
        final WorkflowDocument workflowDocument = disbursementVoucherDocument.getDocumentHeader().getWorkflowDocument();

        return getCurrentRouteLevels(workflowDocument).contains(DisbursementVoucherConstants.RouteLevelNames.ACCOUNT);
    }
}
