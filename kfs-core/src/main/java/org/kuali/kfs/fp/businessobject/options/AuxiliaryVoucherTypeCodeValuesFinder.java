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
package org.kuali.kfs.fp.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

/**
 * <code>{@link KeyValuesBase}</code> class that handles
 * <code>{@link org.kuali.kfs.fp.document.AuxiliaryVoucherDocument}</code> types.
 */
public class AuxiliaryVoucherTypeCodeValuesFinder extends KeyValuesBase {

    /**
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List retval = new ArrayList();
        retval.add(new ConcreteKeyValue(KFSConstants.AuxiliaryVoucher.ADJUSTMENT_DOC_TYPE, KFSConstants.AuxiliaryVoucher.ADJUSTMENT_DOC_TYPE_NAME));
        retval.add(new ConcreteKeyValue(KFSConstants.AuxiliaryVoucher.ACCRUAL_DOC_TYPE, KFSConstants.AuxiliaryVoucher.ACCRUAL_DOC_TYPE_NAME));
        retval.add(new ConcreteKeyValue(KFSConstants.AuxiliaryVoucher.RECODE_DOC_TYPE, KFSConstants.AuxiliaryVoucher.RECODE_DOC_TYPE_NAME));
        return retval;
    }
}
