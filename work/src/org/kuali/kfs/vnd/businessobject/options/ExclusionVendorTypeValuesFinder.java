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
import java.util.List;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.kfs.vnd.VendorParameterConstants;
import org.kuali.kfs.vnd.businessobject.VendorType;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.service.KeyValuesService;

public class ExclusionVendorTypeValuesFinder extends KeyValuesBase{

    private static List<KeyValue> labels = null;

    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    @Override
    public List<KeyValue> getKeyValues() {
        if (labels == null) {
            synchronized (this.getClass()) {
                    KeyValuesService boService = SpringContext.getBean(KeyValuesService.class);
                    Collection<VendorType> codes = boService.findAll(VendorType.class);
                    Collection<String> exclusionCodes = SpringContext.getBean(ParameterService.class).getParameterValuesAsString( KfsParameterConstants.VENDOR_LOOKUP.class, VendorParameterConstants.EXCLUSION_AND_DEBARRED_VENDOR_TYPES);
                    List<KeyValue> tempLabels = new ArrayList<KeyValue>();
                    for (VendorType vt : codes) {
                        if (vt.isActive() && exclusionCodes.contains(vt.getVendorTypeCode())) {
                            tempLabels.add(new ConcreteKeyValue(vt.getVendorTypeCode(), vt.getVendorTypeDescription()));
                        }
                    }
                    labels = tempLabels;
            }
        }
        return labels;
    }
}
