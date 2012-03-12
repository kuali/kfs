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
package org.kuali.kfs.sys.businessobject.options;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.service.KeyValuesService;

/**
 * This class...
 */
public class FiscalYearValuesFinder extends KeyValuesBase {

    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {

        KeyValuesService boService = SpringContext.getBean(KeyValuesService.class);
        FiscalYearComparator fiscalYearComparator = new FiscalYearComparator();
        List optionList = (List) boService.findAll(SystemOptions.class);
        // copy the list of codes before sorting, since we can't modify the results from this method
        if ( optionList == null ) {
            optionList = new ArrayList(0);
        } else {
            optionList = new ArrayList( optionList );
        }
        Collections.sort(optionList, fiscalYearComparator);
        List labels = new ArrayList();
        labels.add(new ConcreteKeyValue("", ""));
        for (Iterator iter = optionList.iterator(); iter.hasNext();) {
            SystemOptions options = (SystemOptions) iter.next();
            labels.add(new ConcreteKeyValue(options.getUniversityFiscalYear().toString(), options.getUniversityFiscalYear().toString()));
        }

        return labels;
    }

}
