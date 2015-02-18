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

import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.businessobject.TravelerDetail;
import org.kuali.kfs.module.tem.document.TravelReimbursementDocument;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.util.ObjectUtils;

public class TravelReimbursementAuthorizer extends TravelArrangeableAuthorizer implements ReturnToFiscalOfficerAuthorizer{

    /**
     *
     * @param reimbursement
     * @param user
     * @return
     */
    public boolean canCertify(final TravelReimbursementDocument reimbursement, Person user) {
        boolean canCertify = false;
        TravelerDetail traveler = reimbursement.getTraveler();
        if (ObjectUtils.isNull(traveler) || !isEmployee(traveler)) {
            canCertify = false;
        } else if (user.getPrincipalId().equals(traveler.getPrincipalId())) {
            canCertify = true;
        }
        return canCertify;
    }

    /**
     * Overridden to add awaiting special request review status, since that happens before FO on TR's
     * @see org.kuali.kfs.module.tem.document.authorization.TravelArrangeableAuthorizer#getNonReturnToFiscalOfficerDocumentStatuses()
     */
    @Override
    protected Set<String> getNonReturnToFiscalOfficerDocumentStatuses() {
        Set<String> appDocStatuses = super.getNonReturnToFiscalOfficerDocumentStatuses();
        appDocStatuses.add(TemConstants.TravelStatusCodeKeys.AWAIT_SPCL);
        return appDocStatuses;
    }

}
