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
package org.kuali.kfs.sys.businessobject.format;

import java.math.BigDecimal;

import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.web.format.BigDecimalFormatter;

/**
 * This class is used to format value to Kuali Decimal objects.
 */
public class KualiDecimalFormatter extends BigDecimalFormatter {

    /**
     * Converts the given String to a KualiDecimal
     */
    @Override
    protected Object convertToObject(String target) {
        BigDecimal value = (BigDecimal)super.convertToObject(target);
        return new KualiDecimal(value);
    }
}
