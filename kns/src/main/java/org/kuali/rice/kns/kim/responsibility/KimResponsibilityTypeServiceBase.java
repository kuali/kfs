/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.kns.kim.responsibility;

import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.kim.api.responsibility.Responsibility;
import org.kuali.rice.kim.framework.responsibility.ResponsibilityTypeService;
import org.kuali.rice.kns.kim.type.DataDictionaryTypeServiceBase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @deprecated A krad integrated type service base class will be provided in the future.
 */
@Deprecated
public class KimResponsibilityTypeServiceBase extends DataDictionaryTypeServiceBase
		implements ResponsibilityTypeService {

	@Override
	public final List<Responsibility> getMatchingResponsibilities( Map<String, String> requestedDetails, List<Responsibility> responsibilitiesList ) {
		if (requestedDetails == null) {
            throw new RiceIllegalArgumentException("requestedDetails is null");
        }

        if (responsibilitiesList == null) {
            throw new RiceIllegalArgumentException("responsibilitiesList is null");
        }

        requestedDetails = translateInputAttributes(requestedDetails);
		validateRequiredAttributesAgainstReceived(requestedDetails);
		return Collections.unmodifiableList(performResponsibilityMatches(requestedDetails, responsibilitiesList));
	}

	/**
	 * Internal method for matching Responsibilities.  Override this method to customize the matching behavior.
	 * 
	 * This base implementation uses the {@link #performMatch(Map, Map)} method
	 * to perform an exact match on the Responsibility details and return all that are equal.
	 */
	protected List<Responsibility> performResponsibilityMatches(Map<String, String> requestedDetails, List<Responsibility> responsibilitiesList) {
		List<Responsibility> matchingResponsibilities = new ArrayList<Responsibility>();
		for (Responsibility responsibility : responsibilitiesList) {
			if ( performMatch(requestedDetails, responsibility.getAttributes())) {
				matchingResponsibilities.add( responsibility );
			}
		}
		return matchingResponsibilities;
	}
}
