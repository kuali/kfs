/*
 * Copyright 2005-2007 The Kuali Foundation
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
package org.kuali.kfs.module.tem.dataaccess;

import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.kfs.module.tem.businessobject.PerDiem;
import org.kuali.kfs.module.tem.businessobject.PrimaryDestination;
import org.kuali.kfs.module.tem.businessobject.TravelAdvance;

/**
 * This is the data access interface for Document objects.
 * 
 */
public interface TravelDocumentDao {

    /**
     *
     * @param travelDocumentNumber to refer to for travel document results
     * @return
     */
	List<String> findDocumentNumbers(final Class<?> travelDocumentClass, final String travelDocumentNumber);
	
	/**
	 * PerDiem lookup base on search values
	 * 
	 * PER_DIEM_LOOKUP_DATE can also be passed in for a custom date duration search
	 * 
	 * @param fieldValues
	 * @return
	 */
	public PerDiem findPerDiem(Map<String, Object> fieldValues);

    /**
     * get all outstanding travel advances by the given invoice document numbers. The advances must have not been used to generate
     * taxable ramification
     * 
     * @param arInvoiceDocNumbers the given AR invoice document numbers
     * @return a list of outstanding travel advances
     */

    List<TravelAdvance> getOutstandingTravelAdvanceByInvoice(Set<String> arInvoiceDocNumber);
    
    public List<PrimaryDestination> findAllDistinctPrimaryDestinations(String tripType);

    public List findDefaultPrimaryDestinations(Class clazz, String countryCode);

    /**
     * find the latest taxable ramification notification date
     * 
     * @return the latest taxable ramification notification date
     */
    Date findLatestTaxableRamificationNotificationDate();

}
