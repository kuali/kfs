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
package org.kuali.kfs.module.cam.document.authorization;

import java.util.Set;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.document.authorization.AccountingDocumentAuthorizerBase;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.KRADConstants;


public class AssetPaymentDocumentAuthorizer extends AccountingDocumentAuthorizerBase {
    
    /**
     * remove accountingperiodviewaction and accountingperiodeditaction actions for
     * asset payment document.
     * @see org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentAuthorizerBase#getDocumentActions(org.kuali.rice.krad.document.Document, org.kuali.rice.kim.api.identity.Person, java.util.Set)
     */
    @Override
    public Set<String> getDocumentActions(Document document, Person user, Set<String> documentActionsFromPresentationController) {
        Set<String> documentActionsToReturn = super.getDocumentActions(document, user, documentActionsFromPresentationController);

        documentActionsToReturn.remove(KFSConstants.YEAR_END_ACCOUNTING_PERIOD_VIEW_DOCUMENT_ACTION);        
        documentActionsToReturn.remove(KFSConstants.YEAR_END_ACCOUNTING_PERIOD_EDIT_DOCUMENT_ACTION);
        
        return documentActionsToReturn;
    }
    
}

