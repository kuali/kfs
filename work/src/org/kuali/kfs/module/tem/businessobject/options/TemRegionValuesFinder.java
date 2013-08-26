/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.businessobject.options;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.tem.businessobject.TemRegion;
import org.kuali.kfs.module.tem.service.TravelService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.service.BusinessObjectService;

public class TemRegionValuesFinder extends KeyValuesBase {

    /**
     * @see org.kuali.rice.krad.keyvalues.KeyValuesFinder#getKeyValues()
     */
    @SuppressWarnings("rawtypes")
    @Override
    public List<KeyValue> getKeyValues() {
        List<KeyValue> keyValues = new ArrayList<KeyValue>();
        keyValues.add(new ConcreteKeyValue("", ""));
        BusinessObjectService service = SpringContext.getBean(BusinessObjectService.class);
        TravelService travelService = SpringContext.getBean(TravelService.class);

        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put("tripTypeCode", "IN");
        List<TemRegion> inRegions = (List<TemRegion>) service.findMatching(TemRegion.class, fieldValues);
        Collections.sort(inRegions);
        fieldValues.put("tripTypeCode", "BLN");
        List<TemRegion> blnRegions = (List<TemRegion>) service.findMatching(TemRegion.class, fieldValues);
        fieldValues.put("tripTypeCode", "OUT");
        List<TemRegion> outRegions = (List<TemRegion>) service.findMatching(TemRegion.class, fieldValues);
        Collections.sort(blnRegions);
        fieldValues.put("tripTypeCode", "INT");
        List<TemRegion> intRegions = (List<TemRegion>) service.findMatching(TemRegion.class, fieldValues);
        Collections.sort(intRegions);


        List<TemRegion> usRegions = new ArrayList<TemRegion>();
        usRegions.addAll(inRegions);
        Collections.sort(blnRegions);
        usRegions.addAll(blnRegions);
        Collections.sort(outRegions);
        usRegions.addAll(outRegions);

        Iterator<TemRegion> it = usRegions.iterator();

        String key = "";
        while (it.hasNext()){
            TemRegion temRegion = it.next();

            String tempKey = temRegion.getRegionName();
            if (!tempKey.equals(key)){
                keyValues.add(new ConcreteKeyValue(temRegion.getRegionCode().toUpperCase(), temRegion.getRegionName().toUpperCase()));
            }
            key = tempKey;
        }
        keyValues.add(new ConcreteKeyValue("---", "------------------------------------------"));
        it = intRegions.iterator();
        while (it.hasNext()) {
            TemRegion temRegion = it.next();
            String tempKey = temRegion.getRegionName();
            if (!tempKey.equals(key)) {
                keyValues.add(new ConcreteKeyValue(temRegion.getRegionCode().toUpperCase(), temRegion.getRegionName().toUpperCase()));
            }
            key = tempKey;
        }

        return keyValues;
    }
}
