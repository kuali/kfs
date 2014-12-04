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
package org.kuali.kfs.module.bc.exception;

import org.kuali.kfs.module.bc.BCKeyConstants;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.krad.exception.AuthorizationException;

/**
 * Handles special Budget Construction Document exception processing to display the reason behind the authorization exception
 * and to also display a link allowing the user to get back to the BC document selection screen.
 * 
 */
public class BudgetConstructionDocumentAuthorizationException extends AuthorizationException {
    
    private boolean isPickListMode;

    /**
     * Constructs a BudgetConstructionDocumentAuthorizationException.java.
     * @param userId
     * @param action
     * @param documentId
     */
    public BudgetConstructionDocumentAuthorizationException(String userId, String action, String documentId, String reason, boolean isPickListMode) {

        super(userId, action, documentId.toString() + ": " + reason, "user '" + userId + "' is not authorized to " + action + " document '" + documentId + ": " + reason + "'", null);
        this.isPickListMode = isPickListMode;
    }

    /**
     * @see org.kuali.rice.krad.exception.AuthorizationException#getErrorMessageKey()
     */
    @Override
    public String getErrorMessageKey() {

        if (isPickListMode){
            return RiceKeyConstants.AUTHORIZATION_ERROR_DOCUMENT;
        } else {
            return BCKeyConstants.ERROR_BUDGET_AUTHORIZATION_DOCUMENT;
        }
    }

}
