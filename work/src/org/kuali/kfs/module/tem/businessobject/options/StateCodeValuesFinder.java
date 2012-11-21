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
import java.util.List;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.location.api.state.State;
import org.kuali.rice.location.api.state.StateService;

public class StateCodeValuesFinder extends KeyValuesBase {

    /**
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    @Override
    public List<KeyValue> getKeyValues() {

        List<State> codes = SpringContext.getBean(StateService.class).findAllStatesInCountry(KFSConstants.COUNTRY_CODE_UNITED_STATES);
        List<KeyValue> keyvalues = new ArrayList<KeyValue>();
        keyvalues.add(new ConcreteKeyValue("", ""));
        for (State state : codes) {
            if(state.isActive()) {
                keyvalues.add(new ConcreteKeyValue(state.getCode(), state.getCode()));
            }
        }
        return keyvalues;
    }

}
