/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.purap.document.validation;

import java.util.List;

import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.document.PurchaseOrderAmendmentDocument;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderTabIdentifierService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;




/**
 *
 * This class validates PurchaseOrder Document for modification of any of the tabs
 * If none is modified then an error message is displayed
 */
public class PurchaseOrderAmendmentEditValidation extends GenericValidation {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurchaseOrderAmendmentEditValidation.class);

    @Override
    public boolean validate(AttributedDocumentEvent event) {

        PurchaseOrderAmendmentDocument doc = (PurchaseOrderAmendmentDocument) event.getDocument();

        return isDocumentEdited(doc);
    }

    /**
     *
     * This method checks whether the document has been edited
     * @param iaDoc
     * @return
     */
    private boolean isDocumentEdited(PurchaseOrderAmendmentDocument poDoc) {

        boolean capitalAssetRequired = false;

        List result = this.getPurchaseOrderTabIdentifierService().getModifiedTabs(poDoc);
        if(result == null || result.size() < 1){
            GlobalVariables.getMessageMap().putError(KFSConstants.DOCUMENT_ERRORS, PurapKeyConstants.ERROR_PURCHASE_ORDER_AMENDMENT_NOT_MODIFIED);
            return false;
        }

        return true;


    }

    private PurchaseOrderTabIdentifierService getPurchaseOrderTabIdentifierService(){
        return SpringContext.getBean(PurchaseOrderTabIdentifierService.class);
    }

}
