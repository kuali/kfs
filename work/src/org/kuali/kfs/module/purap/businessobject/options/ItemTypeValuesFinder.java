/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source$
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
package org.kuali.module.purap.lookup.keyvalues;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.kuali.core.lookup.keyvalues.KeyValuesBase;
import org.kuali.core.service.KeyValuesService;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.web.uidraw.KeyLabelPair;
import org.kuali.module.purap.bo.ItemType;
import org.kuali.module.purap.bo.OwnershipType;

/**
 * This class returns list containg A = Active or I = Inactive
 * 
 */
public class ItemTypeValuesFinder extends KeyValuesBase {

    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */

        public List getKeyValues() {

            KeyValuesService boService = SpringServiceLocator.getKeyValuesService();
            Collection codes = boService.findAll(ItemType.class);
            List defaultAboveTheLine = new ArrayList();
            //TODO: chris - get this from rules through db field
            defaultAboveTheLine.add("ITEM");
            defaultAboveTheLine.add("SRVC");
            
            List labels = new ArrayList();
            for (Object code : codes) {
                ItemType it = (ItemType) code;
                if(defaultAboveTheLine.contains(it.getItemTypeCode())) {
                    labels.add(new KeyLabelPair(it.getItemTypeCode(), it.getItemTypeDescription()));
                }
            }

            

            return labels;
    }

}