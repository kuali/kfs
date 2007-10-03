/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.kra.budget.lookup.keyvalues;

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.lookup.keyvalues.KeyValuesBase;
import org.kuali.core.web.ui.KeyLabelPair;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.kra.KraConstants;
import org.kuali.module.kra.budget.document.BudgetDocument;

public class BudgetManualRateIndicatorValuesFinder extends KeyValuesBase {

    public BudgetManualRateIndicatorValuesFinder() {
        super();
    }

    public List getKeyValues() {
        List ret = new ArrayList();

        ParameterService parameterService = SpringContext.getBean(ParameterService.class);

        ret.add(new KeyLabelPair("N", parameterService.getParameterValue(BudgetDocument.class, KraConstants.KRA_BUDGET_INDIRECT_COST_PROVIDED_SYSTEM)));
        ret.add(new KeyLabelPair("Y", parameterService.getParameterValue(BudgetDocument.class, KraConstants.KRA_BUDGET_INDIRECT_COST_PROVIDED_MANUALLY)));

        return ret;
    }
}