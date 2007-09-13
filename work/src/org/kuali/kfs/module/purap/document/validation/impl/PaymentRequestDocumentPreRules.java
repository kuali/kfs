/*
 * Copyright 2006-2007 The Kuali Foundation.
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

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.document.Document;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapConstants.PREQDocumentsStrings;
import org.kuali.module.purap.document.PaymentRequestDocument;
import org.kuali.module.purap.service.PurapService;

/**
 * Performs prompts and other pre business rule checks for the Payment Request Docuemnt.
 */
public class PaymentRequestDocumentPreRules extends AccountsPayableDocumentPreRulesBase {

    public PaymentRequestDocumentPreRules() {
        super();
    }

    @Override
    public boolean doRules(Document document) {
        boolean preRulesOK = true;
        
        PaymentRequestDocument preq = (PaymentRequestDocument)document;
        if (StringUtils.isBlank(event.getQuestionContext()) || StringUtils.equals(question, PREQDocumentsStrings.THRESHOLD_DAYS_OVERRIDE_QUESTION)) {
            preRulesOK &= confirmPayDayNotOverThresholdDaysAway( preq );
        }
        
        preRulesOK &= super.doRules(document);
        return preRulesOK;
    }
    
    public boolean confirmPayDayNotOverThresholdDaysAway( PaymentRequestDocument preq ) {
        // If the pay date is more than the threshold number of days in the future, ask for confirmation.
        if (!validatePayDateNotOverThresholdDaysAway(preq)) {
            String questionText = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(
                    PurapKeyConstants.WARNING_PAYMENT_REQUEST_PAYDATE_OVER_THRESHOLD_DAYS);
        
            boolean confirmOverride = super.askOrAnalyzeYesNoQuestion(PREQDocumentsStrings.THRESHOLD_DAYS_OVERRIDE_QUESTION, questionText);
            
            // Set a marker to record that this method has been used.
            if (confirmOverride && StringUtils.isBlank(event.getQuestionContext())) {
                event.setQuestionContext(PREQDocumentsStrings.THRESHOLD_DAYS_OVERRIDE_QUESTION);
            }
        
            if (!confirmOverride) {
                event.setActionForwardName(KFSConstants.MAPPING_BASIC);
                return false;
            }
        }
        return true;
    }
    
    public String getDocumentName(){
        return "Payment Request";
    }
    
    /**
     * This method side-effects a warning, and consequently should not be used in such
     * a way as to cause validation to fail. Returns a boolean for ease of testing.  If the
     * threshold days value is positive, the method will test future dates accurately.
     * If the the threshold days value is negative, the method will test past dates.
     * 
     * @param document  The PaymentRequestDocument
     * @return  True if the PREQ's pay date is not over the threshold number of days away. 
     */
    public boolean validatePayDateNotOverThresholdDaysAway(PaymentRequestDocument document) {
        boolean valid = true;
        int thresholdDays = PurapConstants.PREQ_PAY_DATE_DAYS_BEFORE_WARNING;
        if (SpringContext.getBean(PurapService.class).isDateMoreThanANumberOfDaysAway(
                document.getPaymentRequestPayDate(),thresholdDays)) {
            if ( ObjectUtils.isNull(GlobalVariables.getMessageList())) {
                GlobalVariables.setMessageList(new ArrayList());
            }
            GlobalVariables.getMessageList().add(PurapKeyConstants.WARNING_PAYMENT_REQUEST_PAYDATE_OVER_THRESHOLD_DAYS);
            valid &= false;
        }
        return valid;
    }
}
