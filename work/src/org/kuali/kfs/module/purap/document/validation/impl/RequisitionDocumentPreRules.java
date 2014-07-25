/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.purap.document.validation.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.document.service.BudgetAdjustmentLaborBenefitsService;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.businessobject.RequisitionItem;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.purap.document.service.RequisitionService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.GlobalVariables;
/**
 * Business PreRules applicable to Purchasing documents
 */
public class RequisitionDocumentPreRules extends PurchasingDocumentPreRulesBase {

    /**
     * @see org.kuali.rice.kns.rules.PromptBeforeValidationBase#doPrompts(org.kuali.rice.krad.document.Document)
     */
    @Override
    public boolean doPrompts(Document document) {
        boolean preRulesOK = super.doPrompts(document);
        
        PurchasingAccountsPayableDocument purapDocument = (PurchasingAccountsPayableDocument)document;
        
        if (!purapDocument.isUseTaxIndicator()){
            preRulesOK &= checkForTaxRecalculation(purapDocument);
        }
        preRulesOK &= validateAccountingLinesExist((RequisitionDocument)purapDocument);      
        
        return preRulesOK;
    }

    @Override
    protected boolean checkCAMSWarningStatus(PurchasingAccountsPayableDocument purapDocument) {
        return PurapConstants.CAMSWarningStatuses.REQUISITION_STATUS_WARNING_NO_CAMS_DATA.contains(purapDocument.getApplicationDocumentStatus());
    }
    
    /**
     * validate that accounting lines exist, and if not-
     * if there are no content reviewers issue an error, else
     * ask the user if they want to ignore the empty item and let the content reviewer take care of this later
     * 
     */
    @SuppressWarnings("deprecation")
    public boolean validateAccountingLinesExist(RequisitionDocument doc) {
        List<RequisitionItem> itemsMissingAccountingLines = doc.getListOfItemsMissingAccountingLines();
        if (!itemsMissingAccountingLines.isEmpty()) {
            String org = doc.getOrganizationCode();
            String chart = doc.getChartOfAccountsCode();
            if (SpringContext.getBean(RequisitionService.class).hasContentReviewer(org, chart)) {
                GlobalVariables.getMessageMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapConstants.REQ_NO_ACCOUNTING_LINES);
                return false;
            }
            else {
                // Do we want to continue??? Ask the user
                final boolean proceed = super.askOrAnalyzeYesNoQuestion(PurapConstants.REQUISITION_ACCOUNTING_LINES_QUESTION, PurapConstants.QUESTION_REQUISITON_ROUTE_WITHOUT_ACCOUNTING_LINES);
                if (proceed && StringUtils.isBlank(event.getQuestionContext())) {
                    //Set a marker to record that this method has been used.
                    event.setQuestionContext(PurapConstants.REQUISITION_ACCOUNTING_LINES_QUESTION);
                }
                if (!proceed) {
                    // answer is "no, don't continue."
                    List<String> itemsToPrint = new ArrayList<String>(itemsMissingAccountingLines.size());
                    for(RequisitionItem item : itemsMissingAccountingLines){
                        itemsToPrint.add(item.getItemDescription().toString() + "  ");
                    }
                    GlobalVariables.getMessageMap().putWarning(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapConstants.REQ_NO_ACCOUNTING_LINES, itemsToPrint.toString());
                    event.setActionForwardName(KFSConstants.MAPPING_BASIC);
                    return false;
                }

            }
        }
        return true;
    }

}
