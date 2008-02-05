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
package org.kuali.module.purap.rules;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.document.Document;
import org.kuali.core.rules.PreRulesContinuationBase;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapParameterConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.bo.PurchasingItemBase;
import org.kuali.module.purap.bo.RecurringPaymentType;
import org.kuali.module.purap.document.RequisitionDocument;

/**
 * Business PreRules applicable to Purchasing documents
 */
public class RequisitionDocumentPreRules extends PreRulesContinuationBase {

    /**
     * @see org.kuali.core.rules.PreRulesContinuationBase#doRules(org.kuali.core.document.Document)
     */
    @Override
    public boolean doRules(Document document) {
        boolean preRulesOK = true;
        
        RequisitionDocument requisitionDocument = (RequisitionDocument)document;
        
        if (!SpringContext.getBean(ParameterService.class).getIndicatorParameter(RequisitionDocument.class, 
                PurapParameterConstants.CapitalAsset.OVERRIDE_CAPITAL_ASSET_WARNINGS_IND)) {
            if (StringUtils.isBlank(event.getQuestionContext()) || !StringUtils.equals(question, PurapConstants.FIX_CAPITAL_ASSET_WARNINGS)) {
                preRulesOK &= confirmFixCapitalAssetWarningConditions(requisitionDocument);
            }       
        }
        return preRulesOK;
    }
    
    /**
     * Looks for capital asset warning conditions and asks the user for confirmation that he/she will fix the warning conditions,
     * returning to the appropriate page.
     * 
     * @param requisitionDocument   A RequisitionDocument
     * @return  True if the user has indicated that the warnings should be fixed, or if there are no warning conditions.
     */
    public boolean confirmFixCapitalAssetWarningConditions(RequisitionDocument requisitionDocument) {
        boolean proceed = true;

        if (capitalAssetWarningConditionsExist(requisitionDocument)) {
            String questionText = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(
                    PurapKeyConstants.REQ_QUESTION_FIX_CAPITAL_ASSET_WARNINGS)+"<br/><br/>";
            for ( String warning : (List<String>)GlobalVariables.getMessageList() ) {
                questionText += warning+"<br/>";
            }
            proceed = super.askOrAnalyzeYesNoQuestion(PurapConstants.FIX_CAPITAL_ASSET_WARNINGS, questionText);
            
            if (StringUtils.isBlank(event.getQuestionContext())) {
                // Set a marker to record that this method has been used.
                event.setQuestionContext(PurapConstants.FIX_CAPITAL_ASSET_WARNINGS);
            }
            event.setActionForwardName(KFSConstants.MAPPING_BASIC);
        }
        return proceed;
    }
    
    /**
     * Does the capital asset validations for all items, side-effecting the resulting warnings into the GlobalVariables
     * message list.  
     * 
     * @param requisitionDocument   A RequisitionDocument
     * @return  True if capital asset warning conditions exist.
     */
    public boolean capitalAssetWarningConditionsExist(RequisitionDocument requisitionDocument) {
        boolean noWarnings = true;
            
        // Run the validations yielding warnings on failure.                  
        RecurringPaymentType recurringPaymentType = requisitionDocument.getRecurringPaymentType();
        PurchasingDocumentRuleBase ruleBase = new PurchasingDocumentRuleBase();
        
        List<PurchasingItemBase> itemList = requisitionDocument.getItems();    
        for (PurchasingItemBase item : itemList) {
            item.refreshReferenceObject(PurapPropertyConstants.ITEM_TYPE);
            if (item.getItemType().isItemTypeAboveTheLineIndicator()) {
                String identifierString = (item.getItemType().isItemTypeAboveTheLineIndicator() ? "Item " + item.getItemLineNumber().toString() : item.getItemType().getItemTypeDescription());
                
                noWarnings &= ruleBase.processItemCapitalAssetValidation(item, recurringPaymentType, true, identifierString);
            }               
        }
        
        return !noWarnings;
    }

}
