/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.gl.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.gl.Constant;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.valuefinder.ValueFinder;

/**
 * Options finder for the options to include zero encumbrances on the open encumbrance lookup
 */
public class ZeroEncumbranceOptionFinder extends KeyValuesBase implements ValueFinder {

    /**
     * Get the values: to include or exclude the zeroed out encumbrances
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    @Override
    public List<KeyValue> getKeyValues() {
        List<KeyValue> options = new ArrayList<KeyValue>();
        options.add(new ConcreteKeyValue(Constant.ZERO_ENCUMBRANCE_INCLUDE, Constant.ZERO_ENCUMBRANCE_INCLUDE));
        options.add(new ConcreteKeyValue(Constant.ZERO_ENCUMBRANCE_EXCLUDE, Constant.ZERO_ENCUMBRANCE_EXCLUDE));
        return options;
    }

    /**
     * Return the default value - which is to Exclude the zeroed out encumbrances
     * @see org.kuali.rice.kns.lookup.valueFinder.ValueFinder#getValue()
     */
    @Override
    public String getValue() {
        return Constant.ZERO_ENCUMBRANCE_EXCLUDE;
    }

}
