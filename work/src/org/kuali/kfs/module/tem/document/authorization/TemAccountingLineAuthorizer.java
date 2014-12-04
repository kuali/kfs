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
package org.kuali.kfs.module.tem.document.authorization;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.krad.util.KRADConstants;

public class TemAccountingLineAuthorizer extends AccountingLineAuthorizerBase {
    private static Log LOG = LogFactory.getLog(TemAccountingLineAuthorizer.class);

    private DocumentHelperService getDocumentHelperService() {
        return SpringContext.getBean(DocumentHelperService.class);
    }

    /**
     * Overrides the method in AccountingLineAuthorizerBase so that the add button would
     * have the line item number in addition to the rest of the insertxxxx String for
     * methodToCall when the user clicks on the add button.
     *
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase#getAddMethod(org.kuali.kfs.sys.businessobject.AccountingLine, java.lang.String)
     */
    @Override
    protected String getAddMethod(AccountingLine accountingLine, String accountingLineProperty) {
        String infix = getActionInfixForNewAccountingLine(accountingLine, accountingLineProperty);
        if (accountingLineProperty.equals(TemPropertyConstants.ACCOUNT_DISTRIBUTION_NEW_SRC_LINE)) {
            infix = "Distribution";
        }
        return KFSConstants.INSERT_METHOD + infix + "Line.anchoraccounting" + infix + "Anchor";
    }

    /**
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase#getDeleteLineMethod(org.kuali.kfs.sys.businessobject.AccountingLine, java.lang.String, java.lang.Integer)
     */
    @Override
    protected String getDeleteLineMethod(AccountingLine accountingLine, String accountingLineProperty, Integer accountingLineIndex) {
        String infix = getActionInfixForExtantAccountingLine(accountingLine, accountingLineProperty);
        if (accountingLineProperty.contains("Distribution")) {
            infix = "Distribution";
        }
        return KRADConstants.DELETE_METHOD + infix + "Line.line" + accountingLineIndex + ".anchoraccounting" + infix + "Anchor";
    }

    /**
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase#getBalanceInquiryMethod(org.kuali.kfs.sys.businessobject.AccountingLine, java.lang.String, java.lang.Integer)
     */
    @Override
    protected String getBalanceInquiryMethod(AccountingLine accountingLine, String accountingLineProperty, Integer accountingLineIndex) {
        String infix = getActionInfixForExtantAccountingLine(accountingLine, accountingLineProperty);
        if (accountingLineProperty.contains("Distribution")) {
            infix = "Distribution";
        }
        return KFSConstants.PERFORMANCE_BALANCE_INQUIRY_FOR_METHOD + infix + "Line.line" + accountingLineIndex + ".anchoraccounting" + infix + "existingLineLineAnchor" + accountingLineIndex;
    }

    /**
     * Check if workflow is at the specific node
     *
     * @param workflowDocument
     * @param nodeName
     * @return
     */
    public boolean isAtNode(WorkflowDocument workflowDocument, String nodeName) {
        Set<String> nodeNames = workflowDocument.getNodeNames();
        for (String nodeNamesNode : nodeNames) {
            if (nodeName.equals(nodeNamesNode)) {
                return true;
            }
        }
        return false;
    }


}

