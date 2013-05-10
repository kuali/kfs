/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.businessobject.defaultvalue;

import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.AgreementStatus;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.valuefinder.ValueFinder;

public class KEMIDDefaultTransactionRestrictionFinder implements ValueFinder {

    public String getValue() {
        Map pkMap = new HashMap();
        pkMap.put(EndowPropertyConstants.ENDOWCODEBASE_CODE, EndowConstants.AgreementStatusCode.AGRMNT_STAT_CD_PEND);
        AgreementStatus agreementStatus = (AgreementStatus) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(AgreementStatus.class, pkMap);

        if (agreementStatus != null) {
            return agreementStatus.getDefaultTransactionRestrictionCode();
        }
        else {
            return KFSConstants.EMPTY_STRING;
        }
    }
}
