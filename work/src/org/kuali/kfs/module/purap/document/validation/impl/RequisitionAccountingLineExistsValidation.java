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
package org.kuali.kfs.module.purap.document.validation.impl;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.businessobject.RequisitionItem;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.purap.document.service.RequisitionService;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * This class ensures that there are accounting lines on each item. If any item
 * is missing accounting lines, a warning or error is issued.
 */
public class RequisitionAccountingLineExistsValidation extends GenericValidation {

    private RequisitionService requisitionService;

    /**
     * If the organization has a content reviewer, the document is allowed to route (validation suceeds), but
     * a warning is issued. If there is no content reviewer, an error is issued and validation does not succeed.
     *
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        RequisitionDocument doc = (RequisitionDocument) event.getDocument();

        List<RequisitionItem> itemsMissingAccountingLines = doc.getListOfItemsMissingAccountingLines();
        if (!itemsMissingAccountingLines.isEmpty()) {
            String org = doc.getOrganizationCode();
            String chart = doc.getChartOfAccountsCode();
            if (!requisitionService.hasContentReviewer(org, chart)) {
                for(RequisitionItem item : itemsMissingAccountingLines){
                    GlobalVariables.getMessageMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapConstants.REQ_NO_ACCOUNTING_LINES, item.getItemIdentifierString());
                }
                return false;
            }
            // No else clause is needed- this should be handled in the action because we need to confirm with the user. 
            // PromptBeforeValidation didn't work well here for some reason, so we're relying on the action 
        }
        return true;
    }

    public void setRequisitionService(RequisitionService reqs){
        this.requisitionService = reqs;
    }

    public RequisitionService getRequisitionService(){
        return this.requisitionService;
    }
}
