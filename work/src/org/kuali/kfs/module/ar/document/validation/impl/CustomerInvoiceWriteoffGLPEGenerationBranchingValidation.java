/*
 * Copyright 2008-2009 The Kuali Foundation
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
import org.kuali.kfs.module.ar.document.CustomerInvoiceWriteoffDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.BranchingValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;

public class CustomerInvoiceWriteoffGLPEGenerationBranchingValidation extends BranchingValidation {
    
    public static final String IS_CHART_CODE_WRITEOFF_GLPE_VALIDATION = "isChartCodeWriteoffGLPEValidation";
    public static final String IS_ORG_ACCOUNTING_DEFAULT_WRITEOFF_GLPE_VALIDATION = "isOrgAccountingDefaultWriteoffGLPEValidation";
    
    private ParameterService parameterService;
    
    @Override
    protected String determineBranch(AttributedDocumentEvent event) {
        
        String writeoffGLPEGenerationOption = SpringContext.getBean(ParameterService.class).getParameterValueAsString(CustomerInvoiceWriteoffDocument.class, ArConstants.GLPE_WRITEOFF_GENERATION_METHOD);
        
        if (ArConstants.GLPE_WRITEOFF_GENERATION_METHOD_CHART.equals( writeoffGLPEGenerationOption ) ){
            return IS_CHART_CODE_WRITEOFF_GLPE_VALIDATION;
        } else if (ArConstants.GLPE_WRITEOFF_GENERATION_METHOD_ORG_ACCT_DEFAULT.equals(writeoffGLPEGenerationOption)){
            return IS_ORG_ACCOUNTING_DEFAULT_WRITEOFF_GLPE_VALIDATION;
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
