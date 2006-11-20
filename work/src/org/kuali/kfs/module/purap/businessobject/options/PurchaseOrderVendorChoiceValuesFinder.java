/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/purap/businessobject/options/PurchaseOrderVendorChoiceValuesFinder.java,v $
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

import org.kuali.Constants;
import org.kuali.core.lookup.keyvalues.KeyValuesBase;
import org.kuali.core.service.KeyValuesService;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.web.uidraw.KeyLabelPair;
import org.kuali.module.purap.bo.PurchaseOrderVendorChoice;

/**
 * This class returns list containg A = Active or I = Inactive
 * 
 */
public class PurchaseOrderVendorChoiceValuesFinder extends KeyValuesBase {

    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {

        KeyValuesService boService = SpringServiceLocator.getKeyValuesService();
        Collection codes = boService.findAll(PurchaseOrderVendorChoice.class);
        List labels = new ArrayList();
        labels.add(new KeyLabelPair(Constants.EMPTY_STRING, Constants.EMPTY_STRING));
        for (Iterator iter = codes.iterator(); iter.hasNext();) {
            PurchaseOrderVendorChoice povc = (PurchaseOrderVendorChoice) iter.next();
            labels.add(new KeyLabelPair(povc.getPurchaseOrderVendorChoiceCode(), povc.getPurchaseOrderVendorChoiceDescription()));
        }

        return labels;
    }

}