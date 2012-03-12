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
package org.kuali.kfs.fp.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

/**
 * This class returns list of distribute amounts for Assets value pairs.
 */
public class DistributeAmountsForAssetsValuesFinder extends KeyValuesBase {

    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List keyValues = new ArrayList();
        keyValues.add(new ConcreteKeyValue(KFSConstants.CapitalAssets.DISTRIBUTE_COST_EQUALLY_CODE, KFSConstants.CapitalAssets.DISTRIBUTE_COST_EQUALLY_DESCRIPTION));
        keyValues.add(new ConcreteKeyValue(KFSConstants.CapitalAssets.DISTRIBUTE_COST_BY_INDIVIDUAL_ASSET_AMOUNT_CODE, KFSConstants.CapitalAssets.DISTRIBUTE_COST_BY_INDIVIDUAL_ASSET_AMOUNT_DESCRIPTION));

        return keyValues;
    }
}
