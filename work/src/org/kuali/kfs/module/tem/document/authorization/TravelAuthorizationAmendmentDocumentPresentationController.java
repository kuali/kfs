/*
 * Copyright 2013 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document.authorization;

import org.kuali.rice.krad.document.Document;

/**
 * Overrides the can copy to allow the TAA to copy if it is final
 */
public class TravelAuthorizationAmendmentDocumentPresentationController extends TravelAuthorizationDocumentPresentationController {

    /**
     * Overridden to allow copy on default
     * @see org.kuali.rice.krad.document.DocumentPresentationControllerBase#canCopy(org.kuali.rice.krad.document.Document)
     */
    @Override
    public boolean canCopy(Document document) {
        return (document.getDocumentHeader().getWorkflowDocument().isProcessed() || document.getDocumentHeader().getWorkflowDocument().isFinal());
    }

}
