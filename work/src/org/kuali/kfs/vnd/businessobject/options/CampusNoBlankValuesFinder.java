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
package org.kuali.kfs.vnd.businessobject.options;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.businessobject.CampusParameter;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;
import org.kuali.rice.kns.service.KeyValuesService;
import org.kuali.rice.kns.util.KNSPropertyConstants;
import org.kuali.rice.kns.web.ui.KeyLabelPair;

/**
 * Value Finder for Campus with no blank value.
 */
public class CampusNoBlankValuesFinder extends KeyValuesBase {

    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {

        KeyValuesService kvService = SpringContext.getBean(KeyValuesService.class);
        Map fieldValues = new HashMap();
        fieldValues.put(KNSPropertyConstants.ACTIVE, true);
        Collection codes = kvService.findMatching(CampusParameter.class, fieldValues);
        List labels = new ArrayList();
        for (Iterator iter = codes.iterator(); iter.hasNext();) {
            CampusParameter campusParameter = (CampusParameter) iter.next();
            labels.add(new KeyLabelPair(campusParameter.getCampus().getCampusCode(), campusParameter.getCampus().getCampusCode() + " - " + campusParameter.getCampus().getCampusName()));
        }

        return labels;
    }

}
