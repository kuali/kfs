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
package org.kuali.kfs.module.tem.service.impl;

import java.util.List;

import org.kuali.kfs.integration.tem.TravelEntertainmentMovingModuleService;
import org.kuali.kfs.integration.tem.TravelEntertainmentMovingTravelDocument;
import org.kuali.kfs.module.tem.businessobject.AccountingDocumentRelationship;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.TravelReimbursementDocument;
import org.kuali.kfs.module.tem.document.service.AccountingDocumentRelationshipService;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.service.TravelerService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.document.Document;

public class TravelEntertainmentMovingModuleServiceImpl implements TravelEntertainmentMovingModuleService {

	private ParameterService parameterService;
	private TravelDocumentService travelDocumentService;
	private TravelerService travelerService;
	private AccountingDocumentRelationshipService accountingDocumentRelationshipService;

	@Override
	public boolean isTEMDocument(String docId) {
		boolean isTEMDoc = false;
		Document doc;

		doc = find(docId);
		isTEMDoc = doc.getClass().getPackage().getName().contains("org.kuali.kfs.module.tem");

		return isTEMDoc;
	}

	@Override
	public TravelEntertainmentMovingTravelDocument getTEMDocument(String temDocId) {

		TravelEntertainmentMovingTravelDocument doc = null;
		doc = (TravelEntertainmentMovingTravelDocument) find(temDocId);

		return doc;
	}

	private Document find(String temDocId) {
		TravelReimbursementDocument doc = null;
			List<TravelReimbursementDocument> results = travelDocumentService.findReimbursementDocuments(temDocId);
			if(results != null && !results.isEmpty()) {
				doc = results.get(0);
			}
		return doc;
	}

	@Override
	public boolean isTemProfileEmployee(TravelEntertainmentMovingTravelDocument document) {
		return travelerService.isEmployee(((TravelDocument)document).getTraveler());
	}

	@Override
	public boolean isTravelManager(Person user) {
		return travelDocumentService.isTravelManager(user);
	}

    @Override
    public void createAccountingDocumentRelationship(String documentNumber, String relDocumentNumber, String relationDescription) {
        accountingDocumentRelationshipService.save(new AccountingDocumentRelationship(documentNumber, relDocumentNumber, relationDescription));
    }

    @Override
    public boolean isTravelReimbursementDocument(TravelEntertainmentMovingTravelDocument document) {
        return document instanceof TravelReimbursementDocument;
    }

    /**
	 * Sets the parameterService attribute value.
	 * @param parameterService The parameterService to set.
	 */
	public void setParameterService(ParameterService parameterService) {
		this.parameterService = parameterService;
	}

	/**
	 * Gets the parameterService attribute.
	 * @return Returns the parameterService.
	 */
	public ParameterService getParameterService() {
		return parameterService;
	}

	/**
	 * Sets the travelDocumentService attribute value.
	 * @param travelDocumentService The travelDocumentService to set.
	 */
	public void setTravelDocumentService(TravelDocumentService travelDocumentService) {
		this.travelDocumentService = travelDocumentService;
	}

	/**
	 * Gets the travelDocumentService attribute.
	 * @return Returns the travelDocumentService.
	 */
	public TravelDocumentService getTravelDocumentService() {
		return travelDocumentService;
	}

	/**
	 * Sets the travelerService attribute value.
	 * @param travelerService The travelerService to set.
	 */
	public void setTravelerService(TravelerService travelerService) {
		this.travelerService = travelerService;
	}

	/**
	 * Gets the travelerService attribute.
	 * @return Returns the travelerService.
	 */
	public TravelerService getTravelerService() {
		return travelerService;
	}

    public AccountingDocumentRelationshipService getAccountingDocumentRelationshipService() {
        return accountingDocumentRelationshipService;
    }

    public void setAccountingDocumentRelationshipService(AccountingDocumentRelationshipService accountingDocumentRelationshipService) {
        this.accountingDocumentRelationshipService = accountingDocumentRelationshipService;
    }

}
