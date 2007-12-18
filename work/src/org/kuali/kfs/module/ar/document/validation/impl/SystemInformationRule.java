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
package org.kuali.module.ar.rules;

import org.apache.log4j.Logger;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;

public class SystemInformationRule extends MaintenanceDocumentRuleBase {
    protected static Logger LOG = org.apache.log4j.Logger.getLogger(SystemInformationRule.class);
    
    /**
     * 
     * This method checks that the Sales Tax Objcet Code is of type Income
     * Using the ParameterService to find this valid value?
     * <ul>
     * <li>IC</li>
     * <li>IH</li>
     * <li>CN</li>
     * </ul>
     * @return true if it is an income object
     */
    protected boolean checkSalesTaxObjectValidCode() {
        return true;
    }
    
    /**
     * 
     * This method checks that the Refund Object Code is of type Expense
     * <ul>
     * <li>EE</li>
     * <li>ES</li>
     * <li>EX</li>
     * </ul>
     * @return true if it is an expense object
     */
    protected boolean checkRefundObjectValidCode() {
        return true;
    }

}
