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


import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.cab.CapitalAssetBuilderModuleService;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kns.rules.PromptBeforeValidationBase;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.kns.util.MessageList;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.ErrorMessage;
import org.kuali.rice.krad.util.ObjectUtils;

public abstract class PurapDocumentPreRulesBase extends PromptBeforeValidationBase {

    public PurapDocumentPreRulesBase() {
        super();
    }

    @Override
    public boolean doPrompts(Document document) {
        PurchasingAccountsPayableDocument purapDocument = (PurchasingAccountsPayableDocument)document;
        
        boolean preRulesValid=true;
        
        //refresh accounts in each item....
        List<PurApItem> items = purapDocument.getItems();
        
        for (PurApItem item : items) {
            //refresh the accounts if they do exist...
            for (PurApAccountingLine account : item.getSourceAccountingLines()) {
                account.refreshNonUpdateableReferences();
            }
        }
        
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
        
        StringBuffer questionText = new StringBuffer();
        if (StringUtils.isBlank(event.getQuestionContext())) {
            if (!SpringContext.getBean(CapitalAssetBuilderModuleService.class).warningObjectLevelCapital(purapDocument)) {
                proceed &= false;
                questionText.append(SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(
                        PurapKeyConstants.REQ_QUESTION_FIX_CAPITAL_ASSET_WARNINGS));

                MessageList warnings =  KNSGlobalVariables.getMessageList();
                if ( !warnings.isEmpty() ) {
                    questionText.append("[p]");
                    for ( ErrorMessage warning :  warnings ) {
                        // the following two lines should be used but org.kuali.rice.krad.util.ErrorMessage (line 83) has a bug
                        //questionText.append(warning);
                        //questionText.append("[br]");                        
                        // so, to remove parenthesis in case no params exist   
                        questionText.append(warning.getErrorKey());
                        String[] params = warning.getMessageParameters();
                        if (params != null && params.length > 0) {
                            questionText.append("(");
                            for (int i = 0; i < params.length; ++i) {
                                if (i > 0) {
                                    questionText.append(", ");
                                }
                                questionText.append(params[i]);
                            }
                            questionText.append(")");
                        }
                    }
                    questionText.append("[/p]");
                }                                                        
            }
        }
        
        if (!proceed || ((ObjectUtils.isNotNull(question)) && (question.equals(PurapConstants.FIX_CAPITAL_ASSET_WARNINGS)))) {
            proceed = askOrAnalyzeYesNoQuestion(PurapConstants.FIX_CAPITAL_ASSET_WARNINGS, questionText.toString());
        }
        // Set a marker to record that this method has been used.
        event.setQuestionContext(PurapConstants.FIX_CAPITAL_ASSET_WARNINGS);
        event.setActionForwardName(KFSConstants.MAPPING_BASIC);
        if (proceed) {
            KNSGlobalVariables.getMessageList().clear();
        }
    
        return proceed;
    }

    protected abstract boolean checkCAMSWarningStatus(PurchasingAccountsPayableDocument purapDocument);

}
