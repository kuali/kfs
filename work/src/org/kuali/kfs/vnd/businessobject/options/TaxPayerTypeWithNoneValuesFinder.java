/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.vnd.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.vnd.VendorConstants;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

/**
 * Generator of a list of tax payer type value pairs, including an empty one, as opposed to <code>TaxPayerTypeValuesFinder</code>,
 * which does not include an empty pair.
 * 
 * @see org.kuali.kfs.fp.businessobject.options.TaxPayerTypeValuesFinder
 */
public class TaxPayerTypeWithNoneValuesFinder extends KeyValuesBase {

    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List keyValues = new ArrayList();
        keyValues.add(new ConcreteKeyValue(VendorConstants.TAX_TYPE_FEIN, VendorConstants.TAX_TYPE_FEIN));
        keyValues.add(new ConcreteKeyValue(VendorConstants.TAX_TYPE_SSN, VendorConstants.TAX_TYPE_SSN));
        keyValues.add(new ConcreteKeyValue(KFSConstants.EMPTY_STRING, VendorConstants.NONE));
        return keyValues;
    }

}
