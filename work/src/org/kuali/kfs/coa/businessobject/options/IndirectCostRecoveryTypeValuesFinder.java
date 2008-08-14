/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.coa.businessobject.options;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.kuali.kfs.coa.businessobject.IndirectCostRecoveryType;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;
import org.kuali.rice.kns.service.KeyValuesService;
import org.kuali.rice.kns.web.ui.KeyLabelPair;

/**
 * This class creates a new finder for our forms view (creates a drop-down of {@link ICRTypeCode}s)
 */
public class IndirectCostRecoveryTypeValuesFinder extends KeyValuesBase {

    /**
     * Creates a list of {@link ICRTypeCode}s using their code as their key, and their code "-" name as the display value
     * 
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        KeyValuesService boService = SpringContext.getBean(KeyValuesService.class);
        Collection codes = boService.findAll(IndirectCostRecoveryType.class);

        List sortList = (List) codes;

        // calling comparator.
        IndirectCostRecoveryTypeCodeComparator icrTypeCodeComparator = new IndirectCostRecoveryTypeCodeComparator();

        // sort using comparator.
        Collections.sort(sortList, icrTypeCodeComparator);


        List labels = new ArrayList();
        labels.add(new KeyLabelPair("", ""));


        for (Iterator iter = codes.iterator(); iter.hasNext();) {
            IndirectCostRecoveryType icrType = (IndirectCostRecoveryType) iter.next();

            labels.add(new KeyLabelPair(icrType.getCode(), icrType.getCode() + " - " + icrType.getName()));

        }

        return labels;
    }

}
