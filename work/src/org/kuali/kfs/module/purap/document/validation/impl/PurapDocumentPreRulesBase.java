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
package org.kuali.kfs.module.purap.document.validation.impl;


import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.cab.CapitalAssetBuilderModuleService;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.rules.PromptBeforeValidationBase;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.util.ErrorMessage;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.MessageList;
import org.kuali.rice.kns.util.ObjectUtils;

public abstract class PurapDocumentPreRulesBase extends PromptBeforeValidationBase {

    public PurapDocumentPreRulesBase() {
        super();
    }

    @Override
    public boolean doPrompts(Document document) {
        PurchasingAccountsPayableDocument purapDocument = (PurchasingAccountsPayableDocument)document;
        
        boolean preRulesValid=true;
        
        if (StringUtils.isBlank(event.getQuestionContext()) || StringUtils.equals(question, PurapConstants.FIX_CAPITAL_ASSET_WARNINGS)) {
            preRulesValid &= confirmFixCapitalAssetWarningConditions(purapDocument);
        }
        
        return preRulesValid;
    }

    public boolean confirmFixCapitalAssetWarningConditions(PurchasingAccountsPayableDocument purapDocument) {
        boolean proceed = true;
        
        //check appropriate status first if not in an appropriate status return true
        if(!checkCAMSWarningStatus(purapDocument)) {
            return true;
        }
        
        String questionText = "";
        if (StringUtils.isBlank(event.getQuestionContext())) {
            if (!SpringContext.getBean(CapitalAssetBuilderModuleService.class).warningObjectLevelCapital(purapDocument)) {
                proceed &= false;
                questionText = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(
                        PurapKeyConstants.REQ_QUESTION_FIX_CAPITAL_ASSET_WARNINGS)+"<br/><br/>";
                MessageList warnings =  GlobalVariables.getMessageList();
                if ( !warnings.isEmpty() ) {
                    questionText += "<table class=\"datatable\">";
                    for ( ErrorMessage warning :  warnings ) {
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
    
        return proceed;
    }

    protected abstract boolean checkCAMSWarningStatus(PurchasingAccountsPayableDocument purapDocument);

}