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
package org.kuali.kfs.module.cam.document.web.struts;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.cam.document.AssetTransferDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentFormBase;
import org.kuali.rice.kns.service.BusinessObjectDictionaryService;
import org.kuali.rice.kns.service.DataDictionaryService;

public class AssetTransferForm extends FinancialSystemTransactionalDocumentFormBase {
    private boolean loanNoteAdded;

    public AssetTransferForm() {
        super();
    }

    public AssetTransferDocument getAssetTransferDocument() {
        return (AssetTransferDocument) getDocument();
    }

    @Override
    public void populate(HttpServletRequest request) {
        super.populate(request);
        if (getAssetTransferDocument() != null) {
            DataDictionaryService dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);
            SpringContext.getBean(BusinessObjectDictionaryService.class).performForceUppercase(getAssetTransferDocument());
        }
    }

    @Override
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        super.reset(mapping, request);
        if (getAssetTransferDocument() != null) {
            getAssetTransferDocument().setInterdepartmentalSalesIndicator(false);
        }
    }

    public boolean isLoanNoteAdded() {
        return loanNoteAdded;
    }

    public void setLoanNoteAdded(boolean loanNoteAdded) {
        this.loanNoteAdded = loanNoteAdded;
    }

}
