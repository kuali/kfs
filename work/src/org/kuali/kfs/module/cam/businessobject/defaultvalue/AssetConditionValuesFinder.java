/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.cam.businessobject.defaultvalue;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.cam.businessobject.AssetCondition;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.service.KeyValuesService;

public class AssetConditionValuesFinder extends KeyValuesBase {
    public List<KeyValue> getKeyValues() {
        List<AssetCondition> assetConditions = (List<AssetCondition>) SpringContext.getBean(KeyValuesService.class).findAll(AssetCondition.class);
        // copy the list of codes before sorting, since we can't modify the results from this method
        if ( assetConditions == null ) {
            assetConditions = new ArrayList<AssetCondition>(0);
        } else {
            assetConditions = new ArrayList<AssetCondition>(assetConditions);
        }

        List<KeyValue> labels = new ArrayList<KeyValue>();
        labels.add(new ConcreteKeyValue("", ""));

        for (AssetCondition assetCondition : assetConditions) {
            if(assetCondition.isActive()) {
                labels.add(new ConcreteKeyValue(assetCondition.getAssetConditionCode(), assetCondition.getAssetConditionName()));
            }
        }

        return labels;
    }

}
