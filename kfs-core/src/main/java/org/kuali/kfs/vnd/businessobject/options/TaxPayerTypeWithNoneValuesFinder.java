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
import java.util.List;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.vnd.VendorConstants;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

/**
 * Generator of a list of tax payer type value pairs, including an empty one, as opposed to <code>TaxPayerTypeValuesFinder</code>,
 * which does not include an empty pair.
 * 
 * @see org.kuali.kfs.fp.businessobject.options.TaxPayerTypeValuesFinder
 */
public class TaxPayerTypeWithNoneValuesFinder extends KeyValuesBase {

    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List keyValues = new ArrayList();
        keyValues.add(new ConcreteKeyValue(VendorConstants.TAX_TYPE_FEIN, VendorConstants.TAX_TYPE_FEIN));
        keyValues.add(new ConcreteKeyValue(VendorConstants.TAX_TYPE_SSN, VendorConstants.TAX_TYPE_SSN));
        keyValues.add(new ConcreteKeyValue(KFSConstants.EMPTY_STRING, VendorConstants.NONE));
        return keyValues;
    }

}
