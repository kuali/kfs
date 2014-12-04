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
package org.kuali.kfs.module.tem.document.authorization;

import java.util.Set;

import org.kuali.kfs.module.tem.TemConstants.AgencyAuditSection;
import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentAuthorizerBase;

public class AgencyStagingDataAuthorizer extends FinancialSystemMaintenanceDocumentAuthorizerBase {

        /**
         * @see org.kuali.rice.kns.document.authorization.MaintenanceDocumentAuthorizerBase#getSecurePotentiallyReadOnlySectionIds()
         */
        @Override
        public Set<String> getSecurePotentiallyReadOnlySectionIds() {
            Set<String> readOnlySectionIds = super.getSecurePotentiallyReadOnlySectionIds();
            readOnlySectionIds.add(AgencyAuditSection.airline.name());
            readOnlySectionIds.add(AgencyAuditSection.rentalcar.name());
            readOnlySectionIds.add(AgencyAuditSection.lodging.name());

            return readOnlySectionIds;
        }



}
