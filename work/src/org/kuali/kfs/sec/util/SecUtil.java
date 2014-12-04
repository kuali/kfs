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
package org.kuali.kfs.sec.util;

import org.kuali.kfs.sec.SecConstants;
import org.kuali.kfs.sec.service.AccessSecurityService;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.GlobalVariables;


public class SecUtil {

    /**
     * Calls access security service to check view access on given GLPE for current user. Access to view the GLPE on the document should be related to the view permissions for an
     * accounting line with the same account attributes. Called from generalLedgerPendingEntries.tag
     * 
     * @param pendingEntry GeneralLedgerPendingEntry to check access for
     * @return boolean true if current user has view permission, false otherwise
     */
    public static boolean canViewGLPE(Document document, GeneralLedgerPendingEntry pendingEntry) {
        if (SpringContext.getBean(ConfigurationService.class).getPropertyValueAsBoolean(SecConstants.ACCESS_SECURITY_MODULE_ENABLED_PROPERTY_NAME)) {
            return SpringContext.getBean(AccessSecurityService.class).canViewGLPE(document, pendingEntry, GlobalVariables.getUserSession().getPerson() );
        }
        return true; // access security isn't on.  so that means you can view glpes
    }
}
