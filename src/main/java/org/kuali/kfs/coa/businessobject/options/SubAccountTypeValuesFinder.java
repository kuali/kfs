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
import java.util.List;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

/**
 * This class creates a new finder for our forms view (creates a drop-down of {@link SubAccount}s) It only pulls {@link SubAccount}s
 * that are allowed codes from our {@link ParameterService}
 */
public class SubAccountTypeValuesFinder extends KeyValuesBase {

    /**
     * Creates an allowed list of {@link SubAccount}s from the {@link ParameterService}
     * 
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     * @see ParameterService
     * @see KFSConstants.ChartApcParms.CG_ALLOWED_SUBACCOUNT_TYPE_CODES
     */
    public List getKeyValues() {
        List activeLabels = new ArrayList();
        activeLabels.add(new ConcreteKeyValue("", ""));
        for (String value : KFSConstants.SubAccountType.ELIGIBLE_SUB_ACCOUNT_TYPE_CODES) {
            activeLabels.add(new ConcreteKeyValue(value, value));
        }
        return activeLabels;
    }
}
