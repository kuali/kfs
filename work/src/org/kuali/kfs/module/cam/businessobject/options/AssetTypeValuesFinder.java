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
package org.kuali.kfs.module.cam.businessobject.options;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.kuali.kfs.module.cam.businessobject.AssetType;
import org.kuali.kfs.sys.DynamicCollectionComparator;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;
import org.kuali.rice.kns.service.KeyValuesService;
import org.kuali.rice.core.util.KeyLabelPair;

/**
 * Value Finder for Asset Types.
 */
public class AssetTypeValuesFinder extends KeyValuesBase {

    /**
     * Returns code/description pairs of all Asset Types.
     * 
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        KeyValuesService boService = SpringContext.getBean(KeyValuesService.class);
        Collection codes = boService.findAll(AssetType.class);
        List labels = new ArrayList();
        labels.add(new KeyLabelPair("", ""));
        for (Object code : codes) {
            AssetType at = (AssetType) code;
            labels.add(new KeyLabelPair(at.getCapitalAssetTypeCode(), at.getCapitalAssetTypeDescription()));
        }

        // sort alphabetically by asset type description
        DynamicCollectionComparator.sort(labels, "label");

        return labels;
    }
}
