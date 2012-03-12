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
