/*
 * Copyright 2014 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.businessobject.lookup;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.document.authorization.MaintenanceDocumentPresentationController;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.service.DocumentDictionaryService;

/**
 * Overridden to prevent maintenance of mileage rates which are effectively in the past
 */
public class MileageRateLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {
    protected DocumentDictionaryService documentDictionaryService;

    @Override
    protected boolean allowsMaintenanceEditAction(BusinessObject businessObject) {
        String maintDocTypeName = getMaintenanceDocumentTypeName();

        if (StringUtils.isNotBlank(maintDocTypeName)) {
            final MaintenanceDocumentPresentationController docPresentationController = (MaintenanceDocumentPresentationController)getDocumentDictionaryService().getDocumentPresentationController(maintDocTypeName);
            final boolean allowsEdit = docPresentationController.canMaintain(businessObject);
            if (!allowsEdit) {
                return false;
            }
        }
        return super.allowsMaintenanceEditAction(businessObject);
    }

    public DocumentDictionaryService getDocumentDictionaryService() {
        return documentDictionaryService;
    }

    public void setDocumentDictionaryService(DocumentDictionaryService documentDictionaryService) {
        this.documentDictionaryService = documentDictionaryService;
    }
}
