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
import java.util.List;

import org.kuali.kfs.coa.businessobject.BalanceType;
import org.kuali.kfs.coa.service.BalanceTypeService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

/**
 * This class returns list of balance type value pairs.
 */
public class BalanceTypeValuesFinder extends KeyValuesBase {

    /**
     * Creates a list of {@link BalanceTyp) with their code as their key and display value
     * 
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        Collection<BalanceType> balanceTypeCodeCollection = SpringContext.getBean(BalanceTypeService.class).getAllBalanceTypes();
        List<KeyValue> balanceTypeCodes = new ArrayList<KeyValue>();
        balanceTypeCodes.add(new ConcreteKeyValue("", ""));

        for (BalanceType balanceType : balanceTypeCodeCollection) {
            if(balanceType.isActive()) {
                balanceTypeCodes.add(new ConcreteKeyValue(balanceType.getCode(), balanceType.getCodeAndDescription()));
            }
        }
        return balanceTypeCodes;
    }
}
