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

import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.module.purap.PurapRuleConstants;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.document.VendorCreditMemoDocument;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AddAccountingLineEvent;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.core.api.parameter.ParameterEvaluator;
import org.kuali.rice.core.api.parameter.ParameterEvaluatorService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;

public class VendorCreditMemoObjectCodeValidation extends GenericValidation {

    private ParameterService parameterService;
    
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;
        PurApAccountingLine account = (PurApAccountingLine)((AddAccountingLineEvent)event).getAccountingLine();
        ObjectCode objectCode = account.getObjectCode();

        ParameterEvaluator parameterEvaluator = /*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(VendorCreditMemoDocument.class, PurapRuleConstants.VALID_OBJECT_LEVELS_BY_OBJECT_TYPE_PARM_NM, PurapRuleConstants.INVALID_OBJECT_LEVELS_BY_OBJECT_TYPE_PARM_NM, objectCode.getFinancialObjectTypeCode(), objectCode.getFinancialObjectLevelCode());
        return parameterEvaluator.evaluateAndAddError(SourceAccountingLine.class, "objectCode.financialObjectLevelCode", KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
    }

    public ParameterService getParameterService() {
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

}
