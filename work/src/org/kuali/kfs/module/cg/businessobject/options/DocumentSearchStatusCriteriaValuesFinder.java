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
package org.kuali.kfs.module.cg.businessobject.options;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.kuali.kfs.module.cg.businessobject.AwardStatus;
import org.kuali.kfs.module.cg.businessobject.ProposalStatus;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.service.KeyValuesService;

/**
 * Allows some filtering of document search criteria.
 */
public class DocumentSearchStatusCriteriaValuesFinder extends KeyValuesBase {

    private static final String STATUS_SPACE_PREFIX = "&nbsp;&nbsp;";

    /**
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List<KeyValue> labels = new ArrayList<KeyValue>();
        labels.add(new ConcreteKeyValue("", ""));

        labels.add(new ConcreteKeyValue(" ", "AWARD STATUSES"));
        Collection<AwardStatus> awardCodes = SpringContext.getBean(KeyValuesService.class).findAll(AwardStatus.class);
        for (AwardStatus awardStatus : awardCodes) {
            if (awardStatus.isActive()) {
                labels.add(new ConcreteKeyValue(awardStatus.getAwardStatusCode(), STATUS_SPACE_PREFIX + awardStatus.getAwardStatusCode() + "-" + awardStatus.getAwardStatusDescription()));
            }
        }

        labels.add(new ConcreteKeyValue(" ", "PROPOSAL STATUSES"));
        Collection<ProposalStatus> proposalCodes = SpringContext.getBean(KeyValuesService.class).findAll(ProposalStatus.class);
        for (ProposalStatus proposalStatus : proposalCodes) {
            if (proposalStatus.isActive()) {
                labels.add(new ConcreteKeyValue(proposalStatus.getProposalStatusCode(), STATUS_SPACE_PREFIX + proposalStatus.getProposalStatusCode() + "-" + proposalStatus.getProposalStatusDescription()));
            }
        }

        return labels;
    }

}
