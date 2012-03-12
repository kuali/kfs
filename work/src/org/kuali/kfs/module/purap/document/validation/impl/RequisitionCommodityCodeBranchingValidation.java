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
package org.kuali.kfs.module.purap.document.validation.impl;

import org.kuali.kfs.module.purap.PurapRuleConstants;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.sys.document.validation.BranchingValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;

public class RequisitionCommodityCodeBranchingValidation extends BranchingValidation {
    
    public static final String COMMODITY_CODE_IS_REQUIRED = "commodityCodeIsRequiredValidation";

    private ParameterService parameterService;
    
    @Override
    protected String determineBranch(AttributedDocumentEvent event) {
        if (parameterService.getParameterValueAsBoolean(RequisitionDocument.class, PurapRuleConstants.ITEMS_REQUIRE_COMMODITY_CODE_IND)) {
            return COMMODITY_CODE_IS_REQUIRED;
        } else {
            return null;
        }
    }

    public ParameterService getParameterService() {
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    
}
