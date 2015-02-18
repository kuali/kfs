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
package org.kuali.kfs.integration.cg.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

/**
 * Builds a list of possible CFDA Maintenance Type ID values used for the CFDA Lookup.
 */
public class CatalogOfFederalDomesticAssistanceMaintenanceTypeIdFinder extends KeyValuesBase {

    public static final String CFDA_MAINTENANCE_MANUAL_TYPE_ID = "MANUAL";
    public static final String CFDA_MAINTENANCE_AUTOMATIC_TYPE_ID = "AUTOMATIC";

    /**
     * Builds a list of possible CFDA Maintenance Type ID values.
     *
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    @Override
    public List<KeyValue> getKeyValues() {

        List<KeyValue> labels = new ArrayList<KeyValue>();
        labels.add(new ConcreteKeyValue("", ""));
        labels.add(new ConcreteKeyValue(CFDA_MAINTENANCE_MANUAL_TYPE_ID, CFDA_MAINTENANCE_MANUAL_TYPE_ID));
        labels.add(new ConcreteKeyValue(CFDA_MAINTENANCE_AUTOMATIC_TYPE_ID, CFDA_MAINTENANCE_AUTOMATIC_TYPE_ID));

        return labels;
    }


}
