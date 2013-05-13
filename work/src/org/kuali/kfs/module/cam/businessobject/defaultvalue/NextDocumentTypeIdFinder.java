/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.cam.businessobject.defaultvalue;

import org.kuali.kfs.module.cam.businessobject.AssetPaymentDocumentType;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.SequenceAccessorService;
import org.kuali.rice.krad.valuefinder.ValueFinder;

public class NextDocumentTypeIdFinder implements ValueFinder {
    /**
     * @see org.kuali.rice.kns.lookup.valueFinder.ValueFinder#getValue()
     */
    @Override
    public String getValue() {
        return getLongValue().toString();
    }

    /**
     * Gets the next sequence number as a long.
     *
     * @return
     */
    public static Long getLongValue() {
        SequenceAccessorService sas = KRADServiceLocator.getSequenceAccessorService();
        return sas.getNextAvailableSequenceNumber("CM_AST_PMT_DOC_TYP_SEQ", AssetPaymentDocumentType.class);
    }

}
