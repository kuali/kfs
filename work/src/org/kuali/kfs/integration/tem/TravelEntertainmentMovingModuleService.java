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

import org.kuali.rice.kim.bo.Person;

public interface TravelEntertainmentMovingModuleService {
	
	public boolean isTEMDocument(String docId);
	
	public TravelEntertainmentMovingTravelDocument getTEMDocument(String temDocId);
	
	public boolean isTEMProfileEmployee(TravelEntertainmentMovingTravelDocument document);

    public boolean isTravelManager(final Person user);
    
    public boolean isTravelReimbursementDocument(TravelEntertainmentMovingTravelDocument document);

    public void createAccountingDocumentRelationship(String documentNumber, String relDocumentNumber, String relationDescription);
}
