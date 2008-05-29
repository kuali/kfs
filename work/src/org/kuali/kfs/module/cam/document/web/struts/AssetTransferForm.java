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
package org.kuali.module.cams.web.struts.form;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.kuali.core.authorization.AuthorizationConstants;
import org.kuali.core.service.BusinessObjectDictionaryService;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.web.struts.form.KualiTransactionalDocumentFormBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.cams.document.AssetTransferDocument;

public class AssetTransferForm extends KualiTransactionalDocumentFormBase {
    private boolean loanNoteAdded;

    public AssetTransferForm() {
        super();
        setDocument(new AssetTransferDocument());
        Map<String, String> editModeMap = new HashMap<String, String>();
        editModeMap.put(AuthorizationConstants.EditMode.FULL_ENTRY, "TRUE");
        setEditingMode(editModeMap);
    }

    public AssetTransferDocument getAssetTransferDocument() {
        return (AssetTransferDocument) getDocument();
    }

    @Override
    public void populate(HttpServletRequest request) {
        super.populate(request);
        DataDictionaryService dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);
        SpringContext.getBean(BusinessObjectDictionaryService.class).performForceUppercase(getAssetTransferDocument());
    }


    public boolean isLoanNoteAdded() {
        return loanNoteAdded;
    }

    public void setLoanNoteAdded(boolean loanNoteAdded) {
        this.loanNoteAdded = loanNoteAdded;
    }

}
