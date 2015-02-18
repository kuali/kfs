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
package org.kuali.kfs.fp.document.service.impl;

import org.kuali.kfs.fp.document.service.TransferOfFundsService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBaseConstants.APPLICATION_PARAMETER;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.core.api.parameter.ParameterEvaluator;
import org.kuali.rice.core.api.parameter.ParameterEvaluatorService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;

/**
 * The default implementation of the TransferOfFundsService
 */
public class TransferOfFundsServiceImpl implements TransferOfFundsService {
    private ParameterService parameterService;

    /**
     * @see org.kuali.kfs.fp.document.service.TransferOfFundsService#isMandatoryTransfersSubType(java.lang.String)
     */
    public boolean isMandatoryTransfersSubType(String objectSubTypeCode) {
        return checkMandatoryTransfersSubType(objectSubTypeCode, APPLICATION_PARAMETER.MANDATORY_TRANSFER_SUBTYPE_CODES);
    }

    /**
     * @see org.kuali.kfs.fp.document.service.TransferOfFundsService#isNonMandatoryTransfersSubType(java.lang.String)
     */
    public boolean isNonMandatoryTransfersSubType(String objectSubTypeCode) {
        return checkMandatoryTransfersSubType(objectSubTypeCode, APPLICATION_PARAMETER.NONMANDATORY_TRANSFER_SUBTYPE_CODES);
    }
    
    /**
     * Helper method for checking the isMandatoryTransfersSubType() and isNonMandatoryTransfersSubType().
     * 
     * @param objectSubTypeCode
     * @param parameterName
     * @return boolean
     */
    protected boolean checkMandatoryTransfersSubType(String objectSubTypeCode, String parameterName) {
        if (objectSubTypeCode == null) {
            throw new IllegalArgumentException("An illegal argument has been passed. Cannot allow (null) subtypes.");
        }
        ParameterEvaluator evaluator = /*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(KfsParameterConstants.FINANCIAL_PROCESSING_DOCUMENT.class, parameterName, objectSubTypeCode);
        boolean returnboolean = evaluator.evaluationSucceeds();
        return returnboolean;
    }

    /**
     * Gets the parameterService attribute. 
     * @return Returns the parameterService.
     */
    public ParameterService getParameterService() {
        return parameterService;
    }

    /**
     * Sets the parameterService attribute value.
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
}
