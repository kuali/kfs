/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.sec.document.authorization;

import java.util.Set;

import org.kuali.kfs.sec.SecPropertyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.document.authorization.MaintenanceDocumentPresentationControllerBase;


/**
 * Presentation controller for the security definition maintenance document, sets conditional read only fields
 */
public class SecurityDefinitionMaintenanceDocumentPresentationController extends MaintenanceDocumentPresentationControllerBase {


    /**
     * Don't allow editing of definition name on edit
     *
     * @see org.kuali.rice.krad.document.authorization.MaintenanceDocumentPresentationControllerBase#getConditionallyReadOnlyPropertyNames(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    public Set<String> getConditionallyReadOnlyPropertyNames(MaintenanceDocument document) {
        Set<String> readOnlyFields = super.getConditionallyReadOnlyPropertyNames(document);

        if (!document.isNew()) {
            readOnlyFields.add(KFSPropertyConstants.NAME);
            readOnlyFields.add(SecPropertyConstants.SECURITY_ATTRIBUTE_NAME_NESTED);
        }

        return readOnlyFields;
    }

}
