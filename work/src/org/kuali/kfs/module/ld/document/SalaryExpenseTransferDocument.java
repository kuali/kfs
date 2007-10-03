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
package org.kuali.module.labor.document;

import java.util.HashMap;
import java.util.Map;

import org.kuali.core.util.KualiDecimal;

/**
 * Document Class for the Salary Expense Transfer Document.
 */
public class SalaryExpenseTransferDocument extends LaborExpenseTransferDocumentBase {
    private Map<String, KualiDecimal> approvalObjectCodeBalances;

    /**
     * Default Constructor.
     */
    public SalaryExpenseTransferDocument() {
        super();

        approvalObjectCodeBalances = new HashMap<String, KualiDecimal>();
    }

    /**
     * Gets the approvalObjectCodeBalances attribute.
     * 
     * @return Returns the approvalObjectCodeBalances.
     */
    public Map<String, KualiDecimal> getApprovalObjectCodeBalances() {
        return approvalObjectCodeBalances;
    }

    /**
     * Sets the approvalObjectCodeBalances attribute value.
     * 
     * @param approvalObjectCodeBalances The approvalObjectCodeBalances to set.
     */
    public void setApprovalObjectCodeBalances(Map<String, KualiDecimal> approvalObjectCodeBalances) {
        this.approvalObjectCodeBalances = approvalObjectCodeBalances;
    }

}
