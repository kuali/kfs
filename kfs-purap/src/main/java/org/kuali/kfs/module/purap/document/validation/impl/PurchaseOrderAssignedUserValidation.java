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
package org.kuali.kfs.module.purap.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;
import org.kuali.rice.krad.util.ObjectUtils;

public class PurchaseOrderAssignedUserValidation extends GenericValidation {

    /**
     * Validation to check that the assigned user exists in the system.
     * @return boolean false if the assigned user doesn't exist in the system.
     */
    public boolean validate(AttributedDocumentEvent event) {
        PurchaseOrderDocument poDocument = (PurchaseOrderDocument)event.getDocument();
        MessageMap errorMap = GlobalVariables.getMessageMap();
        errorMap.clearErrorPath();
        errorMap.addToErrorPath(PurapConstants.DETAIL_TAB_ERRORS);
        boolean valid = true;

        // assigned user is not a required field
        String principalName = poDocument.getAssignedUserPrincipalName();
        if (StringUtils.isEmpty(principalName)) {
            return true;
        }

        // check to see if the person exists in the database
        //if (ObjectUtils.isNull(personService.getPersonByPrincipalName(principalName))) {
        // the following if is equivalent to the above if, since the name->ID conversion is done when the PO form is submit
        // so if ID is null that means no person is found by the name
        if (ObjectUtils.isNull(poDocument.getAssignedUserPrincipalId())) {
            valid = false;
            errorMap.putError(PurapPropertyConstants.ASSIGNED_USER_PRINCIPAL_NAME, PurapKeyConstants.ERROR_NONEXIST_ASSIGNED_USER);
        }

        errorMap.clearErrorPath();
        return valid;
    }

}
