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
package org.kuali.kfs.module.external.kc.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.external.kc.businessobject.Agency;
import org.kuali.kfs.module.external.kc.businessobject.AgencyAddress;
import org.kuali.kfs.module.external.kc.service.ExternalizableBusinessObjectService;
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;

public class SponsorAddressServiceImpl implements ExternalizableBusinessObjectService {

    private ExternalizableBusinessObjectService sponsorService;

    @Override
    public ExternalizableBusinessObject findByPrimaryKey(Map primaryKeys) {
        Agency agency = (Agency) sponsorService.findByPrimaryKey(primaryKeys);
        if (agency != null && agency.getAgencyAddresses() != null && !agency.getAgencyAddresses().isEmpty()) {
            return agency.getAgencyAddresses().get(0);
        } else {
            return null;
        }
    }

    @Override
    public Collection findMatching(Map fieldValues) {
        List<AgencyAddress> results = new ArrayList<AgencyAddress>();
        Collection<Agency> agencies = sponsorService.findMatching(fieldValues);
        for (Agency agency : agencies) {
            results.addAll(agency.getAgencyAddresses());
        }
        return results;
    }

    protected ExternalizableBusinessObjectService getSponsorService() {
        return sponsorService;
    }

    public void setSponsorService(ExternalizableBusinessObjectService sponsorService) {
        this.sponsorService = sponsorService;
    }
}
