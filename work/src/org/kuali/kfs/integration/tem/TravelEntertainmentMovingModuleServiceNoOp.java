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
package org.kuali.kfs.integration.tem;

import org.kuali.rice.kim.api.identity.Person;

public class TravelEntertainmentMovingModuleServiceNoOp implements TravelEntertainmentMovingModuleService {

	@Override
	public boolean isTEMDocument(String docId) {
		return false;
	}

	@Override
	public TravelEntertainmentMovingTravelDocument getTEMDocument(
			String temDocId) {
		return null;
	}

	@Override
	public boolean isTEMProfileEmployee(
			TravelEntertainmentMovingTravelDocument document) {
		return false;
	}

	@Override
	public boolean isTravelManager(Person user) {
		return false;
	}

    @Override
    public boolean isTravelReimbursementDocument(TravelEntertainmentMovingTravelDocument document) {
        return false;
    }

    @Override
    public void createAccountingDocumentRelationship(String documentNumber, String relDocumentNumber, String relationDescription) {
    }

    /**
     * Simply return null here
     * @see org.kuali.kfs.integration.tem.TravelEntertainmentMovingModuleService#getPdpSubUnit()
     */
    @Override
    public String getPdpSubUnit() {
        return null;
    }
}
