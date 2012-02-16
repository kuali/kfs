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

import java.util.List;

import org.kuali.kfs.integration.tem.TravelEntertainmentMovingModuleService;
import org.kuali.kfs.integration.tem.TravelEntertainmentMovingTravelDocument;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.TravelReimbursementDocument;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.service.TravelerService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.service.BusinessObjectService;

public class TravelEntertainmentMovingModuleServiceImpl implements TravelEntertainmentMovingModuleService {

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
			List<TravelReimbursementDocument> results = SpringContext.getBean(TravelDocumentService.class).find(TravelReimbursementDocument.class, temDocId);
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
		return SpringContext.getBean(TravelerService.class).isEmployee(document.getTraveler());
	}

}
