/*
 * Copyright 2014 The Kuali Foundation.
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
package org.kuali.kfs.fp.businessobject.defaultvalue;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.SequenceAccessorService;
import org.kuali.rice.krad.valuefinder.ValueFinder;

/**
 * Finds the next value in the sequence for ProcurementCardDefault records
 */
public class NextProcurementCardDefaultIdFinder implements ValueFinder {
    private static volatile SequenceAccessorService sas;

    /**
     * Pulls the next value from the FP_PRCRMNT_CARD_DFLT_SEQ sequence
     * @see org.kuali.rice.krad.valuefinder.ValueFinder#getValue()
     */
    @Override
    public String getValue() {
        final Long nextValue = getSequenceAccessorService().getNextAvailableSequenceNumber(KFSConstants.PROCUREMENT_CARD_DEFAULT_SEQUENCE_NAME);
        return nextValue.toString();
    }

    public static SequenceAccessorService getSequenceAccessorService() {
        if (sas == null) {
            sas = SpringContext.getBean(SequenceAccessorService.class);
        }
        return sas;
    }
}