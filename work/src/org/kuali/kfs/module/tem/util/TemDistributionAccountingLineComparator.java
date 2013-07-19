/*
 * Copyright 2013 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.util;

import java.util.Comparator;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.businessobject.TemDistributionAccountingLine;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 *
 */
public class TemDistributionAccountingLineComparator implements Comparator<TemDistributionAccountingLine> {

    @Override
    public int compare(TemDistributionAccountingLine dee, TemDistributionAccountingLine dum) {
        if (ObjectUtils.isNull(dee.getObjectCode()) || StringUtils.isBlank(dee.getObjectCode().getFinancialObjectCodeName())) {
            if (ObjectUtils.isNull(dum.getObjectCode()) || StringUtils.isBlank(dum.getObjectCode().getFinancialObjectCodeName())) {
                return 0; // they're both effectively null, so they're equal
            }
            return 1; // dee's still empty, it should go to the top
        }
        if (ObjectUtils.isNull(dum.getObjectCode()) || StringUtils.isBlank(dum.getObjectCode().getFinancialObjectCodeName())) {
            return -1; // dum's empty; it should go to the top
        }
        return dee.getObjectCode().getFinancialObjectCodeName().compareTo(dum.getObjectCode().getFinancialObjectCodeName());
    }

}
