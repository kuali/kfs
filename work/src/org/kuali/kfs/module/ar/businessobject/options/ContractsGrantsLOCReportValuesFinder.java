/*
 * Copyright 2014 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.valuefinder.ValueFinder;

public class ContractsGrantsLOCReportValuesFinder extends KeyValuesBase implements ValueFinder {

    @Override
    public List<KeyValue> getKeyValues() {

        List<KeyValue> keyValues = new ArrayList<KeyValue>();
        keyValues.add(new ConcreteKeyValue(ArConstants.LOCReportTypeFieldValues.AMOUNTS_NOT_DRAWN, ArConstants.LOCReportTypeFieldValues.AMOUNTS_NOT_DRAWN));
        keyValues.add(new ConcreteKeyValue(ArConstants.LOCReportTypeFieldValues.DRAW_DETAILS, ArConstants.LOCReportTypeFieldValues.DRAW_DETAILS));

        return keyValues;
    }

    @Override
    public String getValue() {

        return ArConstants.LOCReportTypeFieldValues.DRAW_DETAILS;
    }

}
