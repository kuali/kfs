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
	public boolean isTemProfileEmployee(
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
}
