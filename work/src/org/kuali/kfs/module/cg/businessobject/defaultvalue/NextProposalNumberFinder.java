/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.cg.lookup.valueFinder;

import org.kuali.core.bo.OriginationCode;
import org.kuali.core.lookup.valueFinder.ValueFinder;
import org.kuali.core.util.SpringServiceLocator;

/**
 * Returns the next Proposal number available.
 * 
 * 
 */
public class NextProposalNumberFinder implements ValueFinder {

    /**
     * @see org.kuali.core.lookup.valueFinder.ValueFinder#getValue()
     */
    public String getValue() {
        final String homeCode = SpringServiceLocator.getHomeOriginationService().getHomeOrigination().getFinSystemHomeOriginationCode();
        // todo: NextSequenceNumberService or the following addition to the Kuali Nervous System, for proper transaction
        // return SpringServiceLocator.getOriginationCodeService().getNextCgProposalNumberAndIncrement(homeCode).toString();
        // Until the Kuali project gets around to resolving this, live dangerously without the transaction...
        OriginationCode o = SpringServiceLocator.getOriginationCodeService().getByPrimaryKey(homeCode);
        Long next = o.getNextCgProposalNumber();
        o.setNextCgProposalNumber(next + 1);
        SpringServiceLocator.getOriginationCodeService().save(o);
        return next.toString();
    }
}
