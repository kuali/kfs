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
package org.kuali.kfs.sys.document.authorization;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kns.bo.user.UniversalUser;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.document.authorization.MaintenanceDocumentAuthorizations;
import org.kuali.rice.kns.document.authorization.MaintenanceDocumentAuthorizerBase;

/**
 * This class is the custom KFS maintenance document authorizer base class
 */
public class FunctionalFieldDescriptionMaintenanceDocumentAuthorizer extends MaintenanceDocumentAuthorizerBase {
    private static Log LOG = LogFactory.getLog(FinancialSystemMaintenanceDocumentAuthorizerBase.class);

    @Override
    public MaintenanceDocumentAuthorizations getFieldAuthorizations(MaintenanceDocument document, UniversalUser user) {
        MaintenanceDocumentAuthorizations maintenanceDocumentAuthorizations = super.getFieldAuthorizations(document, user);
        if (KFSConstants.MAINTENANCE_EDIT_ACTION.equals(((MaintenanceDocument)document).getNewMaintainableObject().getMaintenanceAction())) {
            maintenanceDocumentAuthorizations.addReadonlyAuthField("propertyLabel");
            maintenanceDocumentAuthorizations.addHiddenAuthField("propertyLabelReadOnly");
        }
        if (KFSConstants.MAINTENANCE_NEW_ACTION.equals(((MaintenanceDocument)document).getNewMaintainableObject().getMaintenanceAction())) {
            maintenanceDocumentAuthorizations.addHiddenAuthField("propertyLabel");
        }
        
        if (KFSConstants.MAINTENANCE_COPY_ACTION.equals(((MaintenanceDocument)document).getNewMaintainableObject().getMaintenanceAction())) {
            maintenanceDocumentAuthorizations.addHiddenAuthField("propertyLabel");
        }

        return maintenanceDocumentAuthorizations;
    }
}
