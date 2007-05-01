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
package org.kuali.module.vendor.lookup.keyvalues;

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.lookup.keyvalues.KeyValuesBase;
import org.kuali.core.web.ui.KeyLabelPair;
import org.kuali.kfs.KFSConstants;
import org.kuali.module.vendor.VendorConstants;

/**
 * This class returns list of tax payer type value pairs.
 * 
 * 
 */
public class TaxPayerTypeWithNoneValuesFinder extends KeyValuesBase {

    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List keyValues = new ArrayList();
        keyValues.add(new KeyLabelPair(VendorConstants.TAX_TYPE_FEIN, VendorConstants.TAX_TYPE_FEIN));
        keyValues.add(new KeyLabelPair(VendorConstants.TAX_TYPE_SSN, VendorConstants.TAX_TYPE_SSN));
        keyValues.add(new KeyLabelPair(KFSConstants.EMPTY_STRING, VendorConstants.NONE));
        return keyValues;
    }

}