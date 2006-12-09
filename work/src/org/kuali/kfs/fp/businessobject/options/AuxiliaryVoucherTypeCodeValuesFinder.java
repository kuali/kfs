/*
 * Copyright 2006 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.financial.lookup.keyvalues;

import java.util.ArrayList;
import java.util.List;

import org.kuali.Constants;
import org.kuali.core.lookup.keyvalues.KeyValuesBase;
import org.kuali.core.web.uidraw.KeyLabelPair;

/**
 * <code>{@link KeyValuesBase}</code> class that handles
 * <code>{@link org.kuali.module.financial.document.AuxiliaryVoucherDocument}</code> types.
 * 
 * 
 */
public class AuxiliaryVoucherTypeCodeValuesFinder extends KeyValuesBase {

    /**
     * @see org.kuali.core.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List retval = new ArrayList();
        retval.add(new KeyLabelPair(Constants.AuxiliaryVoucher.ADJUSTMENT_DOC_TYPE, Constants.AuxiliaryVoucher.ADJUSTMENT_DOC_TYPE_NAME));
        retval.add(new KeyLabelPair(Constants.AuxiliaryVoucher.ACCRUAL_DOC_TYPE, Constants.AuxiliaryVoucher.ACCRUAL_DOC_TYPE_NAME));
        retval.add(new KeyLabelPair(Constants.AuxiliaryVoucher.RECODE_DOC_TYPE, Constants.AuxiliaryVoucher.RECODE_DOC_TYPE_NAME));
        return retval;
    }
}
