/*
 * Copyright 2006-2007 The Kuali Foundation.
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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.kuali.core.lookup.keyvalues.KeyValuesBase;
import org.kuali.core.web.ui.KeyLabelPair;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.chart.bo.KualiSystemCode;

/**
 * 
 * This class is the base class for all the ValueFinders for any class extending KualiSystemCode.
 * 
 * Subclasses should extend this, but do nothing. Just extending this class will be sufficient to work.
 * 
 * 
 * 
 */
public abstract class KualiSystemCodeValuesFinder extends KeyValuesBase {

    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {


        // get all the KualiCodeService objects that are associated with this class
        Collection keys = SpringServiceLocator.getKualiCodeService().getAll(this.getValuesClass());
        List keyLabels = new ArrayList();

        // add a blank pair for the first/default key/value pair
        keyLabels.add(new KeyLabelPair("", ""));

        // build the list of code/name combos
        for (Iterator iter = keys.iterator(); iter.hasNext();) {
            KualiSystemCode code = (KualiSystemCode) iter.next();
            keyLabels.add(new KeyLabelPair(code.getCode(), code.getCode() + " - " + code.getName()));
        }

        return keyLabels;
    }

    // must be implemented by the base class, should return the Class of the
    // object being looked up
    protected abstract Class getValuesClass();

}
