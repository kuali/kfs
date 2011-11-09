/*
 * Copyright 2007 The Kuali Foundation
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
