/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
