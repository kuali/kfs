/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.kns.document.authorization;

import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;

import java.util.HashSet;
import java.util.Set;

/**
 * Base class for all MaintenanceDocumentPresentationControllers.
 */
public class MaintenanceDocumentPresentationControllerBase extends DocumentPresentationControllerBase
       implements MaintenanceDocumentPresentationController {

    public boolean canCreate(Class boClass) {
        return KRADServiceLocatorWeb.getDocumentDictionaryService().getAllowsNewOrCopy(
                KRADServiceLocatorWeb.getDocumentDictionaryService().getMaintenanceDocumentTypeName(boClass));
    }

    public boolean canMaintain(Object dataObject) {
        return true;
    }

    public Set<String> getConditionallyHiddenPropertyNames(BusinessObject businessObject) {
        return new HashSet<String>();
    }

    public Set<String> getConditionallyHiddenSectionIds(BusinessObject businessObject) {
        return new HashSet<String>();
    }

    @Override
    public Set<String> getConditionallyReadOnlyPropertyNames(MaintenanceDocument document) {
        return new HashSet<String>();
    }

    @Override
    public Set<String> getConditionallyReadOnlySectionIds(MaintenanceDocument document) {
        return new HashSet<String>();
    }

    @Override
    public Set<String> getConditionallyRequiredPropertyNames(MaintenanceDocument document) {
        return new HashSet<String>();
    }
}
