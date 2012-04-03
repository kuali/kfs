/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.module.cab.document.service.impl;

import org.kuali.kfs.module.cab.document.service.GlAndPurApHelperService;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.doctype.DocumentType;
import org.kuali.rice.kew.api.doctype.DocumentTypeService;
import org.kuali.rice.krad.util.KRADConstants;

public class GlAndPurApHelperServiceImpl implements GlAndPurApHelperService {

    @Override
    public String getDocHandlerUrl(String documentNumber, String docTypeName) {
        DocumentTypeService documentTypeService = (DocumentTypeService) KewApiServiceLocator.getDocumentTypeService();
        DocumentType docType = documentTypeService.getDocumentTypeByName(docTypeName);
        String docHandlerUrl = docType.getResolvedDocumentHandlerUrl();
        if (docHandlerUrl.indexOf("?") == -1) {
            docHandlerUrl += "?";
        }
        else {
            docHandlerUrl += "&";
        }

        docHandlerUrl += KRADConstants.PARAMETER_DOC_ID + "=" + documentNumber + "&" + KRADConstants.PARAMETER_COMMAND + "=" + KewApiConstants.DOCSEARCH_COMMAND;
        return docHandlerUrl;
    }
}
