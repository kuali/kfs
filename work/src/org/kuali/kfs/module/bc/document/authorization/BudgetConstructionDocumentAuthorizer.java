/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.budget.document.authorization;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.core.authorization.AuthorizationConstants;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.document.authorization.DocumentActionFlags;
import org.kuali.core.document.authorization.DocumentAuthorizerBase;
import org.kuali.core.exceptions.GroupNotFoundException;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.kfs.Constants;
import org.kuali.kfs.document.authorization.AccountingDocumentAuthorizerBase;
import org.kuali.rice.KNSServiceLocator;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.clientapp.vo.ValidActionsVO;

//public class BudgetConstructionDocumentAuthorizer extends AccountingDocumentAuthorizerBase {
public class BudgetConstructionDocumentAuthorizer extends DocumentAuthorizerBase {
    private static Log LOG = LogFactory.getLog(BudgetConstructionDocumentAuthorizer.class);

    /**
     * This inits and returns an editModeMap based on the BC security model
     * 
     * @see org.kuali.core.document.authorization.DocumentAuthorizerBase#getEditMode(org.kuali.core.document.Document, org.kuali.core.bo.user.UniversalUser)
     */
    @Override
    public Map getEditMode(Document d, UniversalUser u) {

//        return super.getEditMode(d, u);

        /*
         * TODO this eventually needs to call service methods that implements the BC security model
         * use FULL_ENTRY for userAtDocLevel, VIEW_ONLY for userAboveDocLevel and 
         * AuthorizationConstants.BudgetConstructionEditMode.USER_BELOW_DOC_LEVEL for limited access
         * use AuthorizationConstants.BudgetConstructionEditMode.SYSTEM_VIEW_ONLY to reflect when BC itself is in viewonly mode
         */ 
        Map editModeMap = new HashMap();
        String editMode = AuthorizationConstants.EditMode.FULL_ENTRY;
        editModeMap.put(editMode, "TRUE");

        return editModeMap;
    }

    public Map getEditMode(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String subAccountNumber, UniversalUser u){
        
        /*
         * TODO this eventually needs to call service methods that implements the BC security model
         * use FULL_ENTRY for userAtDocLevel, VIEW_ONLY for userAboveDocLevel and 
         * AuthorizationConstants.BudgetConstructionEditMode.USER_BELOW_DOC_LEVEL for limited access
         * use AuthorizationConstants.BudgetConstructionEditMode.SYSTEM_VIEW_ONLY to reflect when BC itself is in viewonly mode
         */ 
        Map editModeMap = new HashMap();
        String editMode = AuthorizationConstants.EditMode.FULL_ENTRY;
        editModeMap.put(editMode, "TRUE");

        return editModeMap;
    }

    /**
     * @see org.kuali.core.document.authorization.DocumentAuthorizerBase#getDocumentActionFlags(org.kuali.core.document.Document, org.kuali.core.bo.user.UniversalUser)
     */
    @Override
    public DocumentActionFlags getDocumentActionFlags(Document document, UniversalUser user) {
//TODO this needs to call service methods that implements the BC security model
//        return super.getDocumentActionFlags(document, user);
            LOG.debug("calling BudgetConstructionDocumentAuthorizer.getDocumentActionFlags for document '" + document.getDocumentNumber() + "'. user '" + user.getPersonUserIdentifier() + "'");

            DocumentActionFlags flags = new DocumentActionFlags(); // all flags default to false

            flags.setCanClose(true);
            flags.setCanSave(true);

            //TODO is this needed for BC??
            setAnnotateFlag(flags);

            return flags;
    }


}
