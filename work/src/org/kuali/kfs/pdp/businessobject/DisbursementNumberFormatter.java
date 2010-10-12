/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.pdp.businessobject;
import java.math.BigDecimal;
import java.text.NumberFormat;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.KualiInteger;
import org.kuali.rice.kns.util.RiceKeyConstants;
import org.kuali.rice.kns.web.format.CurrencyFormatter;
import org.kuali.rice.kns.web.format.FormatException;
import org.kuali.rice.kns.web.format.Formatter;

public class DisbursementNumberFormatter extends CurrencyFormatter {
    /*
     * Copyright 2006-2007 The Kuali Foundation
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

    protected Object convertToObject(String target) {
        if (StringUtils.isEmpty(target))
            return null;
         return new KualiInteger(target);
    }

    /**
     * Returns a string representation of its argument formatted as a currency value.
     */
    public Object format(Object obj) {
        return (obj == null ? null : obj.toString());
    }
}