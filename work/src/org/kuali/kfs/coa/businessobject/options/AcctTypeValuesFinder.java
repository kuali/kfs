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
import java.util.Collections;
import java.util.List;

import org.kuali.kfs.coa.businessobject.AccountType;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.service.KeyValuesService;

/**
 * This class creates a new finder for our forms view (creates a drop-down of Account types)
 */
public class AcctTypeValuesFinder extends KeyValuesBase {

    /**
     * Creates a list of {@link AcctType}s using their code as their key, and their code "-" account type name as the display value
     * 
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List<KeyValue> getKeyValues() {

        List<AccountType> codes = (List<AccountType>) SpringContext.getBean(KeyValuesService.class).findAll(AccountType.class);
        // copy the list of codes before sorting, since we can't modify the results from this method
        if ( codes == null ) {
            codes = new ArrayList<AccountType>(0);
        } else {
            codes = new ArrayList<AccountType>( codes );
        }
        // sort using comparator.
        Collections.sort(codes, new AccountTypeCodeComparator());

        List<KeyValue> labels = new ArrayList<KeyValue>();
        labels.add(new ConcreteKeyValue("", ""));

        for (AccountType acctType : codes) {
            if(acctType.isActive()) {
                labels.add(new ConcreteKeyValue(acctType.getAccountTypeCode(), acctType.getAccountTypeCode() + " - " + acctType.getAccountTypeName()));
            }
        }

        return labels;
    }


}
