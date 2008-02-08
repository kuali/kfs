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
import org.kuali.core.question.ConfirmationQuestion;
import org.kuali.core.rules.PreRulesContinuationBase;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapParameterConstants;
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
        
        if (StringUtils.isBlank(event.getQuestionContext()) || StringUtils.equals(question, PurapConstants.FIX_CAPITAL_ASSET_WARNINGS)) {
            preRulesOK &= confirmFixCapitalAssetWarningConditions(requisitionDocument);
        }
        
        return preRulesOK;
    }
    
    /**
     * Analogous to similarly-named methods in Rule classes.  Loops through the items and runs validations
     * applying only to items.
     * 
     * @param requisitionDocument
     * @return
     */
    public boolean processItemValidation(RequisitionDocument requisitionDocument) {
        boolean valid = true;                         
        RecurringPaymentType recurringPaymentType = requisitionDocument.getRecurringPaymentType();
              
        List<PurchasingItemBase> itemList = requisitionDocument.getItems();    
        for (PurchasingItemBase item : itemList) {
            if (item.getItemType().isItemTypeAboveTheLineIndicator()) {
                String identifierString = (item.getItemType().isItemTypeAboveTheLineIndicator() ? "Item " + item.getItemLineNumber().toString() : item.getItemType().getItemTypeDescription());
                
                if (capitalAssetWarningConditionsExist(item, recurringPaymentType, identifierString)) {
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
     * @param requisitionDocument   A RequisitionDocument
     * @return  True if the user has indicated that the warnings should be fixed, or if there are no warning conditions.
     */
    public boolean confirmFixCapitalAssetWarningConditions(RequisitionDocument requisitionDocument) {
        boolean proceed = true;
        
        if (!SpringContext.getBean(ParameterService.class).getIndicatorParameter(RequisitionDocument.class, 
                PurapParameterConstants.CapitalAsset.OVERRIDE_CAPITAL_ASSET_WARNINGS_IND)) {
            String questionText = "";
            if (StringUtils.isBlank(event.getQuestionContext())) {
                if (!processItemValidation(requisitionDocument)) {
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
            if (!proceed || question.equals(PurapConstants.FIX_CAPITAL_ASSET_WARNINGS)) {
                proceed &= askOrAnalyzeYesNoQuestion(PurapConstants.FIX_CAPITAL_ASSET_WARNINGS, questionText);
            }
            // Set a marker to record that this method has been used.
            event.setQuestionContext(PurapConstants.FIX_CAPITAL_ASSET_WARNINGS);
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
    public boolean capitalAssetWarningConditionsExist(PurchasingItemBase item, RecurringPaymentType recurringPaymentType, String identifierString) {
        PurchasingDocumentRuleBase ruleBase = new PurchasingDocumentRuleBase();
        return !ruleBase.validateItemCapitalAssetWithWarnings(item, recurringPaymentType, identifierString);        
    }
    
    /**
     * HACK ALERT.  By rights, I shouldn't have to override this, but it doesn't work otherwise.  Most of this
     * is just copied from the superclass.  See discussion in KULPURAP-1961.
     * 
     * @see org.kuali.core.rules.PreRulesContinuationBase#askOrAnalyzeYesNoQuestion(java.lang.String, java.lang.String)
     */
    @Override
    public boolean askOrAnalyzeYesNoQuestion(String id, String text) {

        ContextSession session = new ContextSession(event.getQuestionContext(), event);
        
        if (LOG.isDebugEnabled()) {
            LOG.debug("Entering askOrAnalyzeYesNoQuestion(" + id + "," + text + ")");
        }

        String cached = (String) session.getAttribute(id);
        if (cached != null) {
            LOG.debug("returning cached value: " + id + "=" + cached);
            return new Boolean(cached).booleanValue();
        }

        if (id.equals(question)) {
            session.setAttribute(id, Boolean.toString(!ConfirmationQuestion.NO.equals(buttonClicked)));
            return !ConfirmationQuestion.NO.equals(buttonClicked);
        }
        else if (!session.hasAsked(id)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Forcing question to be asked: " + id);
            }
            session.askQuestion(id, text);
        }

        LOG.debug("Throwing Exception to force return to Action");
        //throw new IsAskingException();
        return true;
    }


}
