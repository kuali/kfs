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
package org.kuali.kfs.gl.businessobject.options;

import org.kuali.kfs.sys.KFSConstants.ParameterValues;
import org.kuali.kfs.sys.businessobject.options.YesNoValuesFinder;
import org.kuali.rice.krad.valuefinder.ValueFinder;

/**
 * An implementation of ValueFinder that allows balance inquiries to choose whether to exclude
 * entries with only C&G Beginning Balances.
 */
public class CGBeginningBalanceExcludeOptionFinder extends YesNoValuesFinder implements ValueFinder {

    /**
     * Gets the default value for this ValueFinder, in this case "Y".
     * @return a String with the default value for this ValueFinder
     * @see org.kuali.rice.krad.valuefinder.ValueFinder#getValue()
     */
    @Override
    public String getValue() {
        return ParameterValues.YES;
    }
}
