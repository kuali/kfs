/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.kra.budget.rules;

import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.kra.KraKeyConstants;
import org.kuali.module.kra.KraPropertyConstants;
import org.kuali.module.kra.budget.bo.IndirectCostLookup;


/**
 * This class...
 */
public class IndirectCostLookupRule extends MaintenanceDocumentRuleBase {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(IndirectCostLookupRule.class);

    private IndirectCostLookup icLookup;

    /**
     * This method performs any necessary custom business rules on the document.
     * 
     * @param document An instance of the maintenance document that is being processed.
     * @return True if all the business rule checks succeed, false otherwise.
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        boolean success = true;

        setupConvenienceObjects();

        success &= super.processCustomSaveDocumentBusinessRules(document);
        success &= validateIndirectCostRate(icLookup);

        return true;
    }

    private boolean validateIndirectCostRate(IndirectCostLookup icLookup) {
        boolean success = true;

        KualiDecimal rate = icLookup.getBudgetIndirectCostRate();
        String costRate = null == rate ? "" : rate.toString();
        // If the cost rate value does not already contain a decimal and two zeros, append them.
        if (costRate.indexOf('.') < 0) {
            costRate = costRate + ".00";
        }
        // If the cost rate value is longer than 6 characters, display an error message
        if (costRate.length() > 6) {
            String[] params = { costRate };
            String propertyName = KraPropertyConstants.DOCUMENT + "." + KraPropertyConstants.NEW_MAINTAINABLE_OBJECT + "." + KraPropertyConstants.COST_RATE;
            GlobalVariables.getErrorMap().putError(propertyName, KraKeyConstants.ERROR_INDIRECT_COST_RATE_MALFORMED, params);
            success = false;
        }

        return success;
    }

    /**
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#setupConvenienceObjects()
     */
    @Override
    public void setupConvenienceObjects() {
        icLookup = (IndirectCostLookup) super.getNewBo();
    }

}
