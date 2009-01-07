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
package org.kuali.kfs.module.purap.document.authorization;

import java.util.Map;

import org.kuali.kfs.sys.document.authorization.AccountingDocumentAuthorizerBase;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.document.Document;

/**
 * Document Authorizer for the Credit Memo document.
 */
public class CreditMemoDocumentAuthorizer extends AccountingDocumentAuthorizerBase {

    /**
     * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizerBase#getEditMode(org.kuali.rice.kns.document.Document,
     *      org.kuali.rice.kim.bo.Person)
     */
    @Override
    public Map getEditMode(Document document, Person user) {
        Map<String, String> editModeMap = super.getEditMode(document, user);

        //TODO hjs more KIM cleanup
//        if (!creditMemoDocument.isUseTaxIndicator() &&
//            !SpringContext.getBean(PurapService.class).isFullDocumentEntryCompleted((VendorCreditMemoDocument) document) &&   
//            KIMServiceLocator.getIdentityManagementService().isMemberOfGroup(user.getPrincipalId(), org.kuali.kfs.sys.KFSConstants.KFS_GROUP_NAMESPACE, apGroup) ) {
//            editModeMap.put(PurapAuthorizationConstants.CreditMemoEditMode.CLEAR_ALL_TAXES, "TRUE");
//        }
        

        return editModeMap;
    }

}

