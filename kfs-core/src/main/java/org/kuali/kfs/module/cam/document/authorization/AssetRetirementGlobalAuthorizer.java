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
package org.kuali.kfs.module.cam.document.authorization;

import java.util.Set;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentAuthorizerBase;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * AssetAuthorizer for Asset edit.
 */
public class AssetRetirementGlobalAuthorizer extends FinancialSystemMaintenanceDocumentAuthorizerBase {

    /**
     * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizerBase#getDocumentActions(org.kuali.rice.kns.document.Document,
     *      org.kuali.rice.kim.bo.Person, java.util.Set)
     */
    @Override
    public Set<String> getDocumentActions(Document document, Person user, Set<String> documentActionsFromPresentationController) {
        Set<String> documentActionsToReturn = super.getDocumentActions(document, user, documentActionsFromPresentationController);

        if (documentActionsToReturn.contains(KRADConstants.KUALI_ACTION_CAN_EDIT) && documentActionsToReturn.contains(KFSConstants.YEAR_END_ACCOUNTING_PERIOD_VIEW_DOCUMENT_ACTION)) {
            // check KIM permission for view
            if (!super.isAuthorized(document, KFSConstants.CoreModuleNamespaces.KFS, KFSConstants.YEAR_END_ACCOUNTING_PERIOD_VIEW_PERMISSION, user.getPrincipalId())) {
                documentActionsToReturn.remove(KFSConstants.YEAR_END_ACCOUNTING_PERIOD_VIEW_DOCUMENT_ACTION);
            }
            // check KIM permission for edit
            else if (super.isAuthorized(document, KFSConstants.CoreModuleNamespaces.KFS, KFSConstants.YEAR_END_ACCOUNTING_PERIOD_EDIT_PERMISSION, user.getPrincipalId())) {
                documentActionsToReturn.add(KFSConstants.YEAR_END_ACCOUNTING_PERIOD_EDIT_DOCUMENT_ACTION);
            }
        }

        return documentActionsToReturn;
    }
}
