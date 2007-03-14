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
package org.kuali.module.chart.lookup.keyvalues;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.kuali.core.lookup.keyvalues.KeyValuesBase;
import org.kuali.core.service.KeyValuesService;
import org.kuali.core.web.ui.KeyLabelPair;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.chart.bo.codes.MandatoryTransferEliminationCode;

/**
 * This class returns list of Budget Aggregation Code type value pairs.
 * 
 * 
 */
public class MandatoryTransferEliminationCodeValuesFinder extends KeyValuesBase {

    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {

        // get a list of all Mandatory Transfer Elimination Codes
        KeyValuesService boService = SpringServiceLocator.getKeyValuesService();
        List mteCodes = (List) boService.findAll(MandatoryTransferEliminationCode.class);

        // calling comparator.
        MandatoryTransferEliminationCodeComparator mteCodeComparator = new MandatoryTransferEliminationCodeComparator();

        // sort using comparator.
        Collections.sort(mteCodes, mteCodeComparator);

        // create a new list (code, descriptive-name)
        List labels = new ArrayList();

        for (Iterator iter = mteCodes.iterator(); iter.hasNext();) {
            MandatoryTransferEliminationCode mteCode = (MandatoryTransferEliminationCode) iter.next();
            labels.add(new KeyLabelPair(mteCode.getCode(), mteCode.getCode() + " - " + mteCode.getName()));
        }

        return labels;
    }

}