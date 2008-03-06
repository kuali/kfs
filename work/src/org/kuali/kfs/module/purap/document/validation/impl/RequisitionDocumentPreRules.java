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
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapParameterConstants;
import org.kuali.module.purap.bo.PurApItem;
import org.kuali.module.purap.bo.PurchasingItemBase;
import org.kuali.module.purap.document.PurchasingAccountsPayableDocument;
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
        
        PurchasingAccountsPayableDocument purapDocument = (PurchasingAccountsPayableDocument)document;
        
        if (StringUtils.isBlank(event.getQuestionContext()) || StringUtils.equals(question, PurapConstants.FIX_CAPITAL_ASSET_WARNINGS)) {
            preRulesOK &= confirmFixCapitalAssetWarningConditions(purapDocument);
        }
        
        return preRulesOK;
    }
    
    /**
     * Analogous to similarly-named methods in Rule classes.  Loops through the items and runs validations
     * applying only to items.
     * 
     * @param purapDocument     A PurchasingAccountsPayableDocument
     * @return  True if all item validations are passed.
     */
    public boolean processItemValidation(PurchasingAccountsPayableDocument purapDocument) {
        boolean valid = true;                                     
        for (PurApItem purApItem : purapDocument.getItems()) {
            PurchasingItemBase item = (PurchasingItemBase)purApItem;
            if (item.getItemType().isItemTypeAboveTheLineIndicator()) {
                if (capitalAssetWarningConditionsExist(purapDocument, item)) {
                    
                    valid &= false;
                }
            }               
        }
        
        return valid;
    }
    
    /**
     * Looks for capital asset warning conditions and asks the user for confirmation that he/she will fix the warning conditions,
     * returning to the appropriate page.
     * 
     * @param purapDocument   A PurchasingAccountsPayableDocument
     * @return  True if the user has indicated that the warnings should be fixed, or if there are no warning conditions.
     */
    public boolean confirmFixCapitalAssetWarningConditions(PurchasingAccountsPayableDocument purapDocument) {
        boolean proceed = true;
        
        if (!SpringContext.getBean(ParameterService.class).getIndicatorParameter(RequisitionDocument.class, 
                PurapParameterConstants.CapitalAsset.OVERRIDE_CAPITAL_ASSET_WARNINGS_IND)) {
            String questionText = "";
            if (StringUtils.isBlank(event.getQuestionContext())) {
                if (!processItemValidation(purapDocument)) {
                    proceed &= false;
                    questionText = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(
                            PurapKeyConstants.REQ_QUESTION_FIX_CAPITAL_ASSET_WARNINGS)+"<br/><br/>";
                    List<String> warnings =  (List<String>)GlobalVariables.getMessageList();
                    if ( !warnings.isEmpty() ) {
                        questionText += "<table class=\"datatable\">";
                        for ( String warning :  warnings ) {
                            questionText += "<tr><td align=left valign=middle class=\"datacell\">"+warning+"</td></tr>";
                        }
                        questionText += "</table>";
                    }                                                        
                }
            }
            if (!proceed || ((ObjectUtils.isNotNull(question)) && (question.equals(PurapConstants.FIX_CAPITAL_ASSET_WARNINGS)))) {
                proceed = askOrAnalyzeYesNoQuestion(PurapConstants.FIX_CAPITAL_ASSET_WARNINGS, questionText);
            }
            // Set a marker to record that this method has been used.
            event.setQuestionContext(PurapConstants.FIX_CAPITAL_ASSET_WARNINGS);
            event.setActionForwardName(KFSConstants.MAPPING_BASIC);
            if (!proceed) {
                GlobalVariables.getMessageList().clear();
            }
        }
        return proceed;
    }
    
    /**
     * Does the capital asset validations for all items, side-effecting the resulting warnings into the GlobalVariables
     * message list.  
     * 
     * @param purapDocument   A PurchasingAccountsPayableDocument
     * @return  True if capital asset warning conditions exist.
     */
    public boolean capitalAssetWarningConditionsExist(PurchasingAccountsPayableDocument purapDocument, PurchasingItemBase item) {
        PurchasingDocumentRuleBase ruleBase = new PurchasingDocumentRuleBase();
        return !ruleBase.validateItemCapitalAssetWithWarnings(purapDocument, item);        
    }
    
}
