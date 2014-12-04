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
package org.kuali.kfs.module.endow.document.authorization;

import java.util.Set;

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowParameterKeyConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.KEMID;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentPresentationControllerBase;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.util.KRADConstants;

public class KEMIDDocumentPresentationController extends FinancialSystemMaintenanceDocumentPresentationControllerBase {


    /**
     * @see org.kuali.rice.krad.document.authorization.MaintenanceDocumentPresentationControllerBase#getConditionallyReadOnlyPropertyNames(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    public Set<String> getConditionallyReadOnlyPropertyNames(MaintenanceDocument document) {

        Set<String> fields = super.getConditionallyReadOnlyPropertyNames(document);
        ParameterService parameterService = SpringContext.getBean(ParameterService.class);

        String kemidValueSystemParam = parameterService.getParameterValueAsString(KEMID.class, EndowParameterKeyConstants.KEMID_VALUE);

        if (EndowConstants.KemidValueOptions.AUTOMATIC.equalsIgnoreCase(kemidValueSystemParam)) {
            fields.add(EndowPropertyConstants.KEMID);
        }

        if (KRADConstants.MAINTENANCE_NEW_ACTION.equals(document.getNewMaintainableObject().getMaintenanceAction()) || KRADConstants.MAINTENANCE_COPY_ACTION.equals(document.getNewMaintainableObject().getMaintenanceAction())) {
            fields.add(EndowPropertyConstants.KEMID_DORMANT_IND);
            fields.add(EndowPropertyConstants.KEMID_CLOSED_IND);

        }
        if (KRADConstants.MAINTENANCE_NEW_ACTION.equals(document.getNewMaintainableObject().getMaintenanceAction())) {

            fields.add(EndowPropertyConstants.KEMID_CLOSED_TO_KEMID);
            fields.add(EndowPropertyConstants.KEMID_CLOSE_CODE);
            fields.add(EndowPropertyConstants.KEMID_DISPOSITION_OF_FUNDS);
            fields.add(EndowPropertyConstants.KEMID_DATE_CLOSED);

        }


        return fields;
    }
}
