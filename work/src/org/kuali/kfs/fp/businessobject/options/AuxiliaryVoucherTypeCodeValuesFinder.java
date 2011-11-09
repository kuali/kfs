/*
 * Copyright 2006 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
