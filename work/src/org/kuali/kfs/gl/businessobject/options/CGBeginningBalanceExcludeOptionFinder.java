/*
 * Copyright 2006 The Kuali Foundation
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

import org.kuali.kfs.sys.KFSConstants.ParameterValues;
import org.kuali.kfs.sys.businessobject.options.YesNoValuesFinder;
import org.kuali.rice.krad.valuefinder.ValueFinder;

/**
 * An implementation of ValueFinder that allows balance inquiries to choose whether to exclude
 * entries with only C&G Beginning Balances.
 */
public class CGBeginningBalanceExcludeOptionFinder extends YesNoValuesFinder implements ValueFinder {

    /**
     * Gets the default value for this ValueFinder, in this case "Y".
     * @return a String with the default value for this ValueFinder
     * @see org.kuali.rice.krad.valuefinder.ValueFinder#getValue()
     */
    @Override
    public String getValue() {
        return ParameterValues.YES;
    }
}
