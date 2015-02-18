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
import java.util.Iterator;
import java.util.List;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.businessobject.OwnershipType;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.service.KeyValuesService;

/**
 * Values Finder for <code>OwnershipType</code>.
 * 
 * @see org.kuali.kfs.vnd.businessobject.OwnershipType
 */
public class VendorOwnershipCodeValuesFinder extends KeyValuesBase {

    /***
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        return getKeyValues(true);
    }

    /***
     * @see org.kuali.rice.krad.keyvalues.KeyValuesBase#getKeyValues(boolean)
     */
    public List getKeyValues(boolean active){
        KeyValuesService boService = SpringContext.getBean(KeyValuesService.class);
        Collection codes;
        List labels = new ArrayList();
        if(active){
            codes = boService.findAll(OwnershipType.class);
            labels.add(new ConcreteKeyValue("", ""));
        } else
            codes = boService.findAllInactive(OwnershipType.class);
        for (Iterator iter = codes.iterator(); iter.hasNext();) {
            OwnershipType ot = (OwnershipType) iter.next();
            labels.add(new ConcreteKeyValue(ot.getVendorOwnershipCode(), ot.getVendorOwnershipDescription()));
        }
        return labels;
    }

}
