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
package org.kuali.kfs.vnd.businessobject.options;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.businessobject.CampusParameter;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.service.KeyValuesService;
import org.kuali.rice.krad.util.KRADPropertyConstants;

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
        fieldValues.put(KRADPropertyConstants.ACTIVE, true);
        Collection codes = kvService.findMatching(CampusParameter.class, fieldValues);
        List labels = new ArrayList();
        for (Iterator iter = codes.iterator(); iter.hasNext();) {
            CampusParameter campusParameter = (CampusParameter) iter.next();
            if(campusParameter.getCampus() != null){
                labels.add(new ConcreteKeyValue(campusParameter.getCampus().getCode(), campusParameter.getCampus().getCode() + " - " + campusParameter.getCampus().getName()));
            }
        }

        return labels;
    }

}
