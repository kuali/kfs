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
package org.kuali.kfs.coa.businessobject.options;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.kuali.kfs.coa.businessobject.CFDA;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.service.KeyValuesService;



/**
 * Allows some information about persisted {@link Cfda} instances to be looked up.
 */
public class CatalogOfFederalDomesticAssistanceMaintenanceTypeIdFinder extends KeyValuesBase {

    public static final String CFDA_MAINTENANCE_MANUAL_TYPE_ID = "MANUAL";
    public static final String CFDA_MAINTENANCE_AUTOMATIC_TYPE_ID = "AUTOMATIC";
    /**
     * Retrieves the list of possible CFDA Maintenance Type IDs and generates a collection with all the possible values.
     *
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    @Override
    public List<KeyValue> getKeyValues() {

        KeyValuesService boService = SpringContext.getBean(KeyValuesService.class);
        Collection<CFDA> codes = boService.findAll(CFDA.class);

        List<KeyValue> labels = new ArrayList<KeyValue>();
        labels.add(new ConcreteKeyValue("", ""));
        labels.add(new ConcreteKeyValue(CFDA_MAINTENANCE_MANUAL_TYPE_ID, CFDA_MAINTENANCE_MANUAL_TYPE_ID));
        labels.add(new ConcreteKeyValue(CFDA_MAINTENANCE_AUTOMATIC_TYPE_ID, CFDA_MAINTENANCE_AUTOMATIC_TYPE_ID));


        return labels;
    }

    
}
