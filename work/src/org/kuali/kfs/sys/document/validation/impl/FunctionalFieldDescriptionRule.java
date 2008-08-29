/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.kfs.sys.document.validation.impl;

import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.FunctionalFieldDescription;
import org.kuali.rice.kns.document.MaintenanceDocument;

public class FunctionalFieldDescriptionRule extends KfsMaintenanceDocumentRuleBase {

    @Override
    protected boolean processCustomApproveDocumentBusinessRules(MaintenanceDocument document) {
        return super.processCustomApproveDocumentBusinessRules(document) && isPropertyValid((FunctionalFieldDescription) getNewBo());
    }

    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        return super.processCustomRouteDocumentBusinessRules(document) && isPropertyValid((FunctionalFieldDescription) getNewBo());
    }

    protected boolean isPropertyValid(FunctionalFieldDescription functionalFieldDescription) {
        functionalFieldDescription.refreshNonUpdateableReferences();
        if (functionalFieldDescription.getBusinessObjectProperty() == null) {
            putFieldError(KFSPropertyConstants.PROPERTY_LABEL, KFSKeyConstants.ERROR_EXISTENCE, getDdService().getAttributeLabel(FunctionalFieldDescription.class, KFSPropertyConstants.PROPERTY_NAME));
            return false;
        }
        return true;
    }
}
