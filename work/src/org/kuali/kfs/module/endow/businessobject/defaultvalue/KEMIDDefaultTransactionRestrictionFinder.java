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
