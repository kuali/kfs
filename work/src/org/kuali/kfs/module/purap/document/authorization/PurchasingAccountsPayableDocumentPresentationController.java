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
package org.kuali.kfs.module.purap.document.authorization;

import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationControllerBase;
import org.kuali.rice.kns.document.Document;


public class PurchasingAccountsPayableDocumentPresentationController extends FinancialSystemTransactionalDocumentPresentationControllerBase {

    /**
     * None of the PURAP documents allowing editing by adhoc requests
     * 
     * @see org.kuali.rice.kns.document.authorization.DocumentPresentationControllerBase#canEdit(org.kuali.rice.kns.document.Document)
     */
    @Override
    protected boolean canEdit(Document document) {
        if (document.getDocumentHeader().getWorkflowDocument().isAdHocRequested()) {
            return false;
        }
        return super.canEdit(document);
    }
    
}
