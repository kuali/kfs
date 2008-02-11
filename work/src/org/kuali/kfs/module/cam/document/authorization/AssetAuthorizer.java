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
package org.kuali.module.cams.document.authorization;

import java.util.Map;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.document.authorization.DocumentActionFlags;
import org.kuali.core.document.authorization.MaintenanceDocumentActionFlags;
import org.kuali.core.document.authorization.MaintenanceDocumentAuthorizations;
import org.kuali.core.document.authorization.MaintenanceDocumentAuthorizerBase;
import org.kuali.module.cams.bo.Asset;


/**
 * Authorizer class for Asset maintenance document
 */
public class AssetAuthorizer extends MaintenanceDocumentAuthorizerBase {

    /**
     * By default, there are no restrictions for the fields in the superclass. This method is overridden here to makes all the
     * fields in Asset Header readOnly if the Asset is not a parent. If the Asset is not a new Asset, if it is a parent, if
     * Asset header and Asset type is not null and if the Asset type's changed allowed is set to N in the Asset type maintenance
     * table, then we have to set the Asset type as readOnly field.
     * 
     * @see org.kuali.core.document.authorization.MaintenanceDocumentAuthorizer#getFieldAuthorizations(org.kuali.core.document.MaintenanceDocument,
     *      org.kuali.core.bo.user.UniversalUser)
     */
    @Override
    public MaintenanceDocumentAuthorizations getFieldAuthorizations(MaintenanceDocument document, UniversalUser user) {
        MaintenanceDocumentAuthorizations auths = new MaintenanceDocumentAuthorizations();
        return auths;
    }

    /**
     * If the current user is a member of TAXNBR_ACCESSIBLE_GROUP then user is allowed to edit tax number.
     * 
     * @see org.kuali.core.document.MaintenanceDocumentAuthorizerBase#getEditMode(org.kuali.core.document.Document,
     *      org.kuali.core.bo.user.KualiUser)
     */
    @Override
    public Map getEditMode(Document document, UniversalUser user) {
        Map editMode = super.getEditMode(document, user);
        Asset asset = (Asset) ((MaintenanceDocument) document).getNewMaintainableObject().getBusinessObject();
        
        return editMode;
    }

    /**
     * Disables blanket approve for Asset maintenance document
     * 
     * @see org.kuali.core.document.authorization.DocumentAuthorizer#getDocumentActionFlags(org.kuali.core.document.Document,
     *      org.kuali.core.bo.user.UniversalUser)
     */
    @Override
    public DocumentActionFlags getDocumentActionFlags(Document document, UniversalUser user) {
        MaintenanceDocumentActionFlags docActionFlags = new MaintenanceDocumentActionFlags(super.getDocumentActionFlags(document, user));
        
        return docActionFlags;
    }
  
}
