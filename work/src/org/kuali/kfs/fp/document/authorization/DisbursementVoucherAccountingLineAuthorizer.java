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

