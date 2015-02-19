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
package org.kuali.kfs.coa.businessobject.options;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.krad.bo.KualiCodeBase;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.service.KeyValuesService;

/**
 * This class is the base class for all the ValueFinders for any class extending KualiSystemCode. Subclasses should extend this, but
 * do nothing. Just extending this class will be sufficient to work.
 */
public abstract class KualiSystemCodeValuesFinder extends KeyValuesBase {

    /**
     * Calls getValuesClass() to generate a list of key/value pairs from the {@link KualiCodeBase}'s code as the key and the code
     * and description as the value
     * 
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     * @return list of key/value pairs for displaying on the client side
     */
    public List getKeyValues() {

        // get all the KualiCodeService objects that are associated with this class
        Collection businessObjects = SpringContext.getBean(KeyValuesService.class).findAll(this.getValuesClass());
        List keyLabels = new ArrayList();

        // add a blank pair for the first/default key/value pair
        keyLabels.add(new ConcreteKeyValue("", ""));

        // build the list of code/name combos
        for (Iterator iter = businessObjects.iterator(); iter.hasNext();) {
            KualiCodeBase businessObject = (KualiCodeBase) iter.next();
            keyLabels.add(new ConcreteKeyValue(businessObject.getCode(), businessObject.getCodeAndDescription()));
        }

        return keyLabels;
    }

    /**
     * This method must be implemented by the base class, should return the Class of the object being looked up
     * 
     * @return class of object being looked up
     */
    protected abstract Class getValuesClass();

}
