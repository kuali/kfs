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
package org.kuali.kfs.fp.document.service.impl;

import org.kuali.kfs.fp.document.service.TransferOfFundsService;
import org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBaseConstants.APPLICATION_PARAMETER;
import org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBaseConstants.EXCEPTIONS;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.kns.service.ParameterEvaluator;
import org.kuali.rice.kns.service.ParameterService;

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
    private final boolean checkMandatoryTransfersSubType(String objectSubTypeCode, String parameterName) {
        if (objectSubTypeCode == null) {
            throw new IllegalArgumentException(EXCEPTIONS.NULL_OBJECT_SUBTYPE_MESSAGE);
        }
        ParameterEvaluator evaluator = getParameterService().getParameterEvaluator(KfsParameterConstants.FINANCIAL_PROCESSING_DOCUMENT.class, parameterName, objectSubTypeCode);
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
