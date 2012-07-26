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
package org.kuali.kfs.module.tem.service.impl;

import static org.kuali.kfs.module.tem.TemConstants.PARAM_NAMESPACE;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.integration.tem.TravelEntertainmentMovingModuleService;
import org.kuali.kfs.integration.tem.TravelEntertainmentMovingTravelDocument;
import org.kuali.kfs.module.tem.TemConstants.TravelAuthorizationParameters;
import org.kuali.kfs.module.tem.TemConstants.TravelEntertainmentParameters;
import org.kuali.kfs.module.tem.TemConstants.TravelParameters;
import org.kuali.kfs.module.tem.TemConstants.TravelReimbursementParameters;
import org.kuali.kfs.module.tem.TemConstants.TravelRelocationParameters;
import org.kuali.kfs.module.tem.businessobject.AccountingDocumentRelationship;
import org.kuali.kfs.module.tem.document.TravelReimbursementDocument;
import org.kuali.kfs.module.tem.document.service.AccountingDocumentRelationshipService;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.service.TravelerService;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.service.ParameterService;

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
		try {
			List<TravelReimbursementDocument> results = travelDocumentService.find(TravelReimbursementDocument.class, temDocId);
			if(results != null && !results.isEmpty()) {
				doc = results.get(0);
			}
		} catch (WorkflowException ex) {
			ex.printStackTrace();
		}
		return doc;
	}

	@Override
	public boolean isTEMProfileEmployee(TravelEntertainmentMovingTravelDocument document) {
		return travelerService.isEmployee(document.getTraveler());
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
