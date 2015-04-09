/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
