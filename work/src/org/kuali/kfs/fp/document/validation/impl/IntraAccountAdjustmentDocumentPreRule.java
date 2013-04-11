/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.fp.document.validation.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.fp.document.IntraAccountAdjustmentDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kns.rules.PromptBeforeValidationBase;
import org.kuali.rice.krad.document.Document;

public class IntraAccountAdjustmentDocumentPreRule extends PromptBeforeValidationBase{

    private Logger logger = Logger.getLogger(IntraAccountAdjustmentDocumentPreRule.class);
    private static String WARNING_MSG = "You have made no corrections to either the sub-account or sub-object code; is this correct ?" ;


    /**
     * Checks to find at least one AL with both Sub Account and Sub Object code empty.
     * If there are no accounting lines in  the document this method will return true.
     * @see org.kuali.rice.kns.rules.PromptBeforeValidationBase#doPrompts(org.kuali.rice.krad.document.Document)
     */
    @Override
    public boolean doPrompts(Document document) {
        boolean continueRoute = true;
        boolean userClickedYes = false;

        IntraAccountAdjustmentDocument iaaDocument = (IntraAccountAdjustmentDocument)document;

        List<AccountingLine> accountingLines = new ArrayList<AccountingLine>();
        accountingLines.addAll(iaaDocument.getSourceAccountingLines());
        accountingLines.addAll(iaaDocument.getTargetAccountingLines());

        if(! accountingLines.isEmpty()) {
            Collection<AccountingLine> acctLines = findAccountingLineWithEmptySubAcctSubObjCode(accountingLines);
            if(acctLines != null && acctLines.size() > 0) {
                continueRoute = false;

                userClickedYes = super.askOrAnalyzeYesNoQuestion("IAA_WARNING", getWarningMessage(document));

                if(!userClickedYes) {
                    this.event.setActionForwardName(KFSConstants.MAPPING_BASIC);
                }
            }
        }

        return continueRoute ? true : userClickedYes;
    }

    private String getWarningMessage(Document document) {
        String questionText = "";
        if(getParamterService().parameterExists(document.getClass(),
                    KFSKeyConstants.IntraAccountAdjustment.WARNING_NO_SUB_ACCT_SUB_OBJ_FRM_PARAM)) {
            questionText = getParamterService().getParameterValueAsString(document.getClass(),
                    KFSKeyConstants.IntraAccountAdjustment.WARNING_NO_SUB_ACCT_SUB_OBJ_FRM_PARAM);
        }
        else {
            questionText = this.getConfigService().getPropertyValueAsString(
                    KFSKeyConstants.IntraAccountAdjustment.WARNING_NO_SUB_ACCT_SUB_OBJ_FRM_PARAM);
        }

        if(StringUtils.isEmpty(questionText)) {
            questionText = WARNING_MSG;
        }

        return questionText;
    }

    private Collection<AccountingLine> findAccountingLineWithEmptySubAcctSubObjCode(List<AccountingLine> accountingLines){
        List<AccountingLine> acctLines =new ArrayList<AccountingLine>();
        for(AccountingLine accountingLine :accountingLines){
            if(accountingLine.getSubAccountNumber() == null && accountingLine.getSubObjectCode().getFinancialSubObjectCode() == null){
                acctLines.add(accountingLine);
            }
        }

        return acctLines;
    }


    private ParameterService getParamterService() {
        return SpringContext.getBean(ParameterService.class);
    }

    private ConfigurationService getConfigService() {
        return SpringContext.getBean(ConfigurationService.class);
    }
}
