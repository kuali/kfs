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
