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

import org.kuali.kfs.module.tem.TemConstants.TravelReimbursementParameters;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.TravelReimbursementDocument;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.core.api.parameter.ParameterEvaluator;
import org.kuali.rice.core.api.parameter.ParameterEvaluatorService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.util.GlobalVariables;

public class ReimbursementAccountingObjectLevelValidation extends GenericValidation {

    private ParameterService parameterService;
    private ParameterEvaluatorService parameterEvaluatorService;

    @Override
    public boolean validate(AttributedDocumentEvent event) {
        // Skip object code validation if the travel document is of TravelAuthorizationDocument type
        if (event.getDocument() instanceof TravelAuthorizationDocument) {
            return true;
        }

        boolean rulePassed = true;
        GlobalVariables.getMessageMap().clearErrorPath();
        GlobalVariables.getMessageMap().addToErrorPath(TemPropertyConstants.NEW_SOURCE_ACCTG_LINE);

        // If the Accounting Distribution tab is disabled, use object level validation
        final boolean showAccountDistribution = parameterService.getParameterValueAsBoolean(TravelReimbursementDocument.class, TravelReimbursementParameters.DISPLAY_ACCOUNTING_DISTRIBUTION_TAB_IND);
        if (!showAccountDistribution) {
            ParameterEvaluator parameterEvaluator = parameterEvaluatorService.getParameterEvaluator(TravelReimbursementDocument.class, TravelReimbursementParameters.OBJECT_LEVELS);
            rulePassed = parameterEvaluator.evaluateAndAddError(SourceAccountingLine.class, "objectCode.financialObjectLevelCode", KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        }

        GlobalVariables.getMessageMap().removeFromErrorPath(TemPropertyConstants.NEW_SOURCE_ACCTG_LINE);

        return rulePassed;
    }

    public ParameterService getParameterService() {
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public ParameterEvaluatorService getParameterEvaluatorService() {
        return parameterEvaluatorService;
    }

    public void setParameterEvaluatorService(ParameterEvaluatorService parameterEvaluatorService) {
        this.parameterEvaluatorService = parameterEvaluatorService;
    }

}
