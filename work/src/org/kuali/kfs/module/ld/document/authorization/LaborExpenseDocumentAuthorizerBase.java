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
package org.kuali.module.labor.document.authorization;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.document.authorization.DocumentActionFlags;
import org.kuali.core.document.authorization.TransactionalDocumentActionFlags;
import org.kuali.kfs.document.authorization.AccountingDocumentAuthorizerBase;

/**
 * Implements special document authorization for the labor expense documents.
 */
public class LaborExpenseDocumentAuthorizerBase extends AccountingDocumentAuthorizerBase {

    /**
     * Override to disallow copy and error correction.
     * 
     * @see org.kuali.core.document.authorization.TransactionalDocumentAuthorizerBase#getDocumentActionFlags(org.kuali.core.document.Document,
     *      org.kuali.core.bo.user.UniversalUser)
     */
    @Override
    public DocumentActionFlags getDocumentActionFlags(Document document, UniversalUser user) {
        TransactionalDocumentActionFlags flags = (TransactionalDocumentActionFlags) super.getDocumentActionFlags(document, user);

        flags.setCanCopy(false);
        flags.setCanErrorCorrect(false);

        return flags;
    }

}
