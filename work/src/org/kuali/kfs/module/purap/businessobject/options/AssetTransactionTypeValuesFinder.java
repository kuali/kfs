/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.kfs.module.purap.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.integration.cab.CapitalAssetBuilderAssetTransactionType;
import org.kuali.kfs.integration.cab.CapitalAssetBuilderModuleService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

/**
 * Values finder for CapitalAssetBuilderAssetTransactionTypes
 */
public class AssetTransactionTypeValuesFinder extends KeyValuesBase {

    /**
     * Returns code/description pairs of all CapitalAssetBuilderAssetTransactionTypes.
     * 
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List<CapitalAssetBuilderAssetTransactionType> types = SpringContext.getBean(CapitalAssetBuilderModuleService.class).getAllAssetTransactionTypes();
        List labels = new ArrayList<KeyValue>();
        labels.add(new ConcreteKeyValue("",""));
        for (Object type : types) {
            CapitalAssetBuilderAssetTransactionType camsType = (CapitalAssetBuilderAssetTransactionType)type;           
            labels.add(new ConcreteKeyValue(camsType.getCapitalAssetTransactionTypeCode(), camsType.getCapitalAssetTransactionTypeDescription()));
        }

        return labels;
    }

}
