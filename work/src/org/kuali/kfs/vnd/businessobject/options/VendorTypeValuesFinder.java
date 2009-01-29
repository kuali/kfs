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
import java.util.List;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.businessobject.VendorType;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;
import org.kuali.rice.kns.service.KeyValuesService;
import org.kuali.rice.kns.web.ui.KeyLabelPair;

/**
 * Values Finder for <code>VendorType</code>.
 * 
 * @see org.kuali.kfs.vnd.businessobject.VendorType
 */
public class VendorTypeValuesFinder extends KeyValuesBase {

    private static List<KeyLabelPair> labels = null;
    
    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List<KeyLabelPair> getKeyValues() {
        if ( labels == null ) {
            KeyValuesService boService = SpringContext.getBean(KeyValuesService.class);
            Collection<VendorType> codes = boService.findAll(VendorType.class);
            labels = new ArrayList<KeyLabelPair>();
            labels.add(new KeyLabelPair("", ""));
            for (VendorType vt : codes) {
                if ( vt.isActive() ) {
                    labels.add(new KeyLabelPair(vt.getVendorTypeCode(), vt.getVendorTypeDescription()));
                }
            }
        }

        return labels;
    }

}
