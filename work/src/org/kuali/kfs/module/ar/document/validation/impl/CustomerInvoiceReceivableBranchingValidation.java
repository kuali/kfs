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
