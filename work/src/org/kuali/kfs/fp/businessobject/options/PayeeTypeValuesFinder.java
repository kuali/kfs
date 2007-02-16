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
package org.kuali.module.financial.lookup.keyvalues;

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.lookup.keyvalues.KeyValuesBase;
import org.kuali.core.web.ui.KeyLabelPair;
import org.kuali.module.financial.rules.DisbursementVoucherRuleConstants;

/**
 * This class returns list of payee type value pairs.
 * 
 * 
 */
public class PayeeTypeValuesFinder extends KeyValuesBase {

    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List keyValues = new ArrayList();
        keyValues.add(new KeyLabelPair(DisbursementVoucherRuleConstants.DV_PAYEE_TYPE_PAYEE, DisbursementVoucherRuleConstants.DV_PAYEE_TYPE_PAYEE + " - DV Payee"));
        keyValues.add(new KeyLabelPair(DisbursementVoucherRuleConstants.DV_PAYEE_TYPE_EMPLOYEE, DisbursementVoucherRuleConstants.DV_PAYEE_TYPE_EMPLOYEE + " - Employee"));
        // keyValues.add(new KeyLabelPair(Constants.DV_PAYEE_TYPE_VENDOR, "Vendor"));

        return keyValues;
    }

}
