/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.gl.service.impl.orgreversion;

import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.gl.service.OrganizationReversionCategoryLogic;

public class CashOrganizationReversionCategoryLogic implements OrganizationReversionCategoryLogic {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CashOrganizationReversionCategoryLogic.class);

    static final public String NAME = "Cash";
    static final public String CODE = "CASH";

    public boolean containsObjectCode(ObjectCode oc) {
        LOG.debug("containsObjectCode() started");

        if (oc.getChartOfAccounts().getFinancialCashObject().equals(oc.getObjectCode())) {
            return true;
        }
        return false;
    }

    public String getCode() {
        return CODE;
    }

    public String getName() {
        return NAME;
    }

    public boolean isExpense() {
        return false;
    }
}
