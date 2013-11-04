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
package org.kuali.kfs.module.endow.document.web.struts;

import javax.servlet.http.HttpServletRequest;

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.document.CorpusAdjustmentDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.BusinessObjectDictionaryService;

public class CorpusAdjustmentDocumentForm extends EndowmentTransactionLinesDocumentFormBase {

    public CorpusAdjustmentDocumentForm() {
        super();
        setSourceGroupLabelName(EndowConstants.DECREASE_TRANSACTION_LINE_GROUP_LABEL_NAME);
        setTargetGroupLabelName(EndowConstants.INCREASE_TRANSACTION_LINE_GROUP_LABEL_NAME);

        // don't show these values on the edoc.
        setShowIncomeTotalAmount(false);
        setShowIncomeTotalUnits(false);
        setShowPrincipalTotalUnits(false);

        // set the drop-down value to P-Principal and make the field readonly
        getNewSourceTransactionLine().setTransactionIPIndicatorCode(EndowConstants.IncomePrincipalIndicator.PRINCIPAL);
        getNewTargetTransactionLine().setTransactionIPIndicatorCode(EndowConstants.IncomePrincipalIndicator.PRINCIPAL);

        setFeildValueToPrincipal(true);

        // do not show the etran code on the UI screen
        setShowETranCode(false);
    }

    @Override
    protected String getDefaultDocumentTypeName() {
        // use the DataDictionaryService service to get the document type name...
        return SpringContext.getBean(DataDictionaryService.class).getDocumentTypeNameByClass(CorpusAdjustmentDocument.class);
    }

    @Override
    public void populate(HttpServletRequest request) {
        super.populate(request);
        SpringContext.getBean(BusinessObjectDictionaryService.class).performForceUppercase(this.getCorpusAdjustmentDocument());
    }

    /**
     * This method gets the Corpus Adjustment document
     * 
     * @return the CorpusAdjustmentDocument
     */
    public CorpusAdjustmentDocument getCorpusAdjustmentDocument() {
        return (CorpusAdjustmentDocument) getDocument();
    }


}
