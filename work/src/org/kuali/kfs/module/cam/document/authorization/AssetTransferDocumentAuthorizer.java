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
package org.kuali.module.cams.document.authorization;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.document.authorization.DocumentActionFlags;
import org.kuali.kfs.document.authorization.AccountingDocumentAuthorizerBase;

public class AssetTransferDocumentAuthorizer extends AccountingDocumentAuthorizerBase {

    @Override
    public void canInitiate(String documentTypeName, UniversalUser user) {
        super.canInitiate(documentTypeName, user);
    }

    @Override
    public DocumentActionFlags getDocumentActionFlags(Document document, UniversalUser user) {
        // check if transfer action is allowed, if not present an error message
        // check if asset is active
        // check if any pending transactions
        return super.getDocumentActionFlags(document, user);
    }
}
