/*
 * Copyright 2006-2008 The Kuali Foundation
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
package org.kuali.kfs.module.cam.businessobject.options;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.kuali.kfs.module.cam.businessobject.AssetType;
import org.kuali.kfs.sys.DynamicCollectionComparator;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.service.KeyValuesService;

/**
 * Value Finder for Asset Types.
 */
public class AssetTypeValuesFinder extends KeyValuesBase {

    /**
     * Returns code/description pairs of all Asset Types.
     * 
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List<KeyValue> getKeyValues() {
        KeyValuesService boService = SpringContext.getBean(KeyValuesService.class);
        Collection<AssetType> assetTypeCodes = boService.findAll(AssetType.class);
        List<KeyValue> keyValueList = new ArrayList<KeyValue>();
        keyValueList.add(new ConcreteKeyValue("", ""));
        for (AssetType at : assetTypeCodes) {
            if(at.isActive()) {
                keyValueList.add(new ConcreteKeyValue(at.getCapitalAssetTypeCode(), at.getCapitalAssetTypeDescription()));
            }
        }

        // sort alphabetically by asset type description
        DynamicCollectionComparator.sort(keyValueList, "value");

        return keyValueList;
    }
}
