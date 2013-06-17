/*
 * Copyright 2012 The Kuali Foundation.
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
