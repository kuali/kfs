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
package org.kuali.kfs.module.external.kc.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.external.kc.businessobject.Agency;
import org.kuali.kfs.module.external.kc.businessobject.AgencyAddress;
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;

public class SponsorAddressServiceImpl extends SponsorServiceImpl {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SponsorAddressServiceImpl.class);

    @Override
    public ExternalizableBusinessObject findByPrimaryKey(Map primaryKeys) {
        Agency agency = (Agency) super.findByPrimaryKey(primaryKeys);
        if (agency != null && agency.getAgencyAddresses() != null && !agency.getAgencyAddresses().isEmpty()) {
            return agency.getAgencyAddresses().get(0);
        } else {
            return null;
        }
    }

    @Override
    public Collection findMatching(Map fieldValues) {
        List<AgencyAddress> results = new ArrayList<AgencyAddress>();
        Collection<Agency> agencies = super.findMatching(fieldValues);
        for (Agency agency : agencies) {
            results.addAll(agency.getAgencyAddresses());
        }
        return results;
    }
}
