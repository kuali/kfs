/*
 * Copyright 2008 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.budget.exceptions;

import org.kuali.RiceKeyConstants;
import org.kuali.core.exceptions.AuthorizationException;
import org.kuali.core.exceptions.DocumentAuthorizationException;
import org.kuali.module.budget.BCKeyConstants;

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

        //super(userId, action, documentId.toString(), "user '" + userId + "' is not authorized to " + action + " document '" + documentId + "'");
        super(userId, action, documentId.toString() + ": " + reason, "user '" + userId + "' is not authorized to " + action + " document '" + documentId + ": " + reason + "'");
        this.isPickListMode = isPickListMode;
    }

    /**
     * @see org.kuali.core.exceptions.AuthorizationException#getErrorMessageKey()
     */
    @Override
    public String getErrorMessageKey() {
//        return RiceKeyConstants.AUTHORIZATION_ERROR_DOCUMENT;
        if (isPickListMode){
            // TODO for now, pick list mode calls regular authorization exception
            // may need to add a window close button?
            return RiceKeyConstants.AUTHORIZATION_ERROR_DOCUMENT;
        } else {
            return BCKeyConstants.ERROR_BUDGET_AUTHORIZATION_DOCUMENT;
        }
    }

}
