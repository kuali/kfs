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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.kfs.fp.document.DisbursementVoucherConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase;
import org.kuali.rice.kew.api.WorkflowDocument;

public class DisbursementVoucherAccountingLineAuthorizer extends AccountingLineAuthorizerBase {
    private static Log LOG = LogFactory.getLog(DisbursementVoucherAccountingLineAuthorizer.class);

    /**
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizer#getEditableBlocksInReadOnlyLine(org.kuali.kfs.sys.document.AccountingDocument,
     *      org.kuali.kfs.sys.businessobject.AccountingLine, org.kuali.rice.kim.api.identity.Person)
     *
     * Overridden to make amount read only for fiscal officer
     */
    @Override
    public boolean determineEditPermissionOnField(AccountingDocument accountingDocument, AccountingLine accountingLine, String accountingLineCollectionProperty, String fieldName, boolean editablePage) {
        boolean canModify = super.determineEditPermissionOnField(accountingDocument, accountingLine, accountingLineCollectionProperty, fieldName, editablePage);
        final WorkflowDocument workflowDocument = accountingDocument.getDocumentHeader().getWorkflowDocument();
        List<String> currentRouteLevels = null;

        currentRouteLevels = new ArrayList<String>(workflowDocument.getNodeNames());
        if (currentRouteLevels.contains(DisbursementVoucherConstants.RouteLevelNames.ACCOUNT)) {
            if (StringUtils.equals(fieldName, getAmountPropertyName())) {  //FO?
                canModify = false;
            }
        }

        return canModify;
    }


    /**
     * @return the property name of the amount field, which will be set read only for fiscal officers
     */
    protected String getAmountPropertyName() {
        return "amount";
    }

}

