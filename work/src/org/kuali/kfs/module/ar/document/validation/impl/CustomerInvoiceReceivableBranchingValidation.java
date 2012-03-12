/*
 * Copyright 2008 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.ar.document.validation.impl;

import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.sys.document.validation.BranchingValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;

public class CustomerInvoiceReceivableBranchingValidation extends BranchingValidation {
    
    public static final String IS_CHART_CODE_RECEIVABLE_VALIDATION = "isChartCodeReceivableValidation";
    public static final String IS_SUB_FUND_GROUP_RECEIVABLE_VALIDATION = "isSubFundGroupReceivableValidation";
    public static final String IS_FAU_RECEIVABLE_VALIDATION = "isFauReceivableValidation";
    
    private ParameterService parameterService;
    
    @Override
    protected String determineBranch(AttributedDocumentEvent event) {
        
        String receivableOffsetOption = parameterService.getParameterValueAsString(CustomerInvoiceDocument.class, ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD);
        if (ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD_CHART.equals(receivableOffsetOption)) {
            return IS_CHART_CODE_RECEIVABLE_VALIDATION;
        } else if (ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD_SUBFUND.equals(receivableOffsetOption)) {
            return IS_SUB_FUND_GROUP_RECEIVABLE_VALIDATION;
        } else if (ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD_FAU.equals( receivableOffsetOption ) ){
            return IS_FAU_RECEIVABLE_VALIDATION;
        }
        return null;
    }

    public ParameterService getParameterService() {
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

}
