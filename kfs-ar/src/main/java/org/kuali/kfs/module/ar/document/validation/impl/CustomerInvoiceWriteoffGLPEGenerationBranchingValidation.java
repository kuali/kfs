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
