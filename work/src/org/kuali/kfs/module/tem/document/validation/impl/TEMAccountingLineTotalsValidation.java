/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document.validation.impl;

import static org.kuali.kfs.module.tem.TemConstants.PARAM_NAMESPACE;
import static org.kuali.kfs.module.tem.TemConstants.TravelReimbursementParameters.PARAM_DTL_TYPE;

import java.util.List;

import org.kuali.kfs.module.tem.TemConstants.TravelReimbursementParameters;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.web.bean.AccountingDistribution;
import org.kuali.kfs.module.tem.service.AccountingDistributionService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;

public class TEMAccountingLineTotalsValidation extends GenericValidation {

    @Override
    public boolean validate(AttributedDocumentEvent event) {
        boolean rulePassed = true;
        TravelDocument travelDocument = (TravelDocument) event.getDocument();
        GlobalVariables.getMessageMap().clearErrorPath();
        
        // If the Accounting Distribution tab is enabled, use it for validation
        boolean showAccountDistribution = false;
        
        if (!(event.getDocument() instanceof TravelAuthorizationDocument)) {
            showAccountDistribution = SpringContext.getBean(ParameterService.class).getIndicatorParameter(PARAM_NAMESPACE, PARAM_DTL_TYPE, TravelReimbursementParameters.ENABLE_ACCOUNTING_DISTRIBUTION_TAB_IND);
        }
        
        if (showAccountDistribution) {
            List<AccountingDistribution> distributions = SpringContext.getBean(AccountingDistributionService.class).buildDistributionFrom(travelDocument);
            KualiDecimal totalRemaining = KualiDecimal.ZERO;
            for (final AccountingDistribution dist : distributions) {
                totalRemaining = totalRemaining.add(dist.getRemainingAmount());                
            }
            
            if (totalRemaining.isGreaterThan(KualiDecimal.ZERO)) {
                GlobalVariables.getMessageMap().putError(TemKeyConstants.TRVL_REIMB_ACCOUNTING_DISTRIBUTION_ERRORS, KFSKeyConstants.ERROR_CUSTOM, "Total Remaining amount should be zero.");
                rulePassed = false;
            }
        }
        else {
            // Accounting Distribution tab is disabled, use the accounting lines for validation
            KualiDecimal reimbursableAmount = travelDocument.getDocumentGrandTotal();
            //KualiDecimal actualReimbursable = travelDocument.getExpenseLimit();
            KualiDecimal sourceAmount = travelDocument.getSourceTotal();

            if (reimbursableAmount != null && reimbursableAmount.isGreaterThan(KualiDecimal.ZERO)) {
                List<SourceAccountingLine> sourceAccountingLines = travelDocument.getSourceAccountingLines();
                
                if (sourceAccountingLines == null || !sourceAmount.isGreaterThan(KualiDecimal.ZERO)) {
                    GlobalVariables.getMessageMap().addToErrorPath(TemPropertyConstants.NEW_SOURCE_ACCTG_LINE);
                    GlobalVariables.getMessageMap().putError(TemPropertyConstants.PAYMENT_CHART_OF_ACCOUNTS_CODE, KFSKeyConstants.ERROR_CUSTOM, "Accounting Line is required");
                    GlobalVariables.getMessageMap().removeFromErrorPath(TemPropertyConstants.NEW_SOURCE_ACCTG_LINE);
                    rulePassed = false;
                }
                else {                
                    if (sourceAccountingLines != null || sourceAmount.isGreaterThan(KualiDecimal.ZERO)) {
//                        if(actualReimbursable != null && actualReimbursable.isGreaterThan(new KualiDecimal(0)) && actualReimbursable.isLessEqual(reimbursableAmount)) {
//                            if (actualReimbursable.equals(sourceAmount)) {
//                                rulePassed = true;
//                            } else {
//                                GlobalVariables.getMessageMap().removeFromErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);
//                                GlobalVariables.getMessageMap().putError(TemPropertyConstants.NEW_SOURCE_ACCTG_LINE, KFSKeyConstants.ERROR_CUSTOM, "Accounting Line total should match reimbursable total of $" + actualReimbursable);
//                                GlobalVariables.getMessageMap().addToErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);
//                                rulePassed = false;
//                            }
//                        } else {
                            if (reimbursableAmount.equals(travelDocument.getDocumentGrandTotal())) {
                                rulePassed = true;
                            } else {
                                GlobalVariables.getMessageMap().removeFromErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);
                                GlobalVariables.getMessageMap().putError(TemPropertyConstants.NEW_SOURCE_ACCTG_LINE, KFSKeyConstants.ERROR_CUSTOM, "Accounting Line total should match document total of $" + travelDocument.getDocumentGrandTotal());
                                GlobalVariables.getMessageMap().addToErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);
                                rulePassed = false;
                            }
//                        }
                    }
                }
            }
        }

        return rulePassed;
    }

}
