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
package org.kuali.kfs.module.cam.businessobject.options;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.kuali.kfs.module.cam.businessobject.AssetPaymentAllocationType;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.service.KeyValuesService;

/**
 * Find all the {@link AssetPaymentDistributionType} values.
 */
public class AssetPaymentAllocationTypeValuesFinder extends KeyValuesBase {


    /**
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List<KeyValue> getKeyValues() {
        KeyValuesService service = SpringContext.getBean(KeyValuesService.class);
        Collection<AssetPaymentAllocationType> codes = service.findAll(AssetPaymentAllocationType.class);
        List<KeyValue> labels = new ArrayList<KeyValue>();
        for (AssetPaymentAllocationType value : codes) {
            labels.add(new ConcreteKeyValue(value.getAllocationCode(), value.getAllocationName()));
        }

        return labels;
    }

}
