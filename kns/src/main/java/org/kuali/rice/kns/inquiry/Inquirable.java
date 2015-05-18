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
package org.kuali.rice.kns.inquiry;

import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.web.ui.Section;
import org.kuali.rice.krad.bo.BusinessObject;

import java.util.List;
import java.util.Map;

/**
 * Defines business logic methods that support the Inquiry framework
 */
@Deprecated
public interface Inquirable extends org.kuali.rice.krad.inquiry.Inquirable {

	@Deprecated
	public void setBusinessObjectClass(Class businessObjectClass);

	@Deprecated
	public BusinessObject getBusinessObject(Map fieldValues);

	@Deprecated
	public HtmlData getInquiryUrl(BusinessObject businessObject,
			String attributeName, boolean forceInquiry);

	@Deprecated
	public String getHtmlMenuBar();

	@Deprecated
	public String getTitle();

	@Deprecated
	public List<Section> getSections(BusinessObject bo);

	@Deprecated
	public void addAdditionalSections(List columns, BusinessObject bo);

	/**
	 * Indicates whether inactive records for the given collection should be
	 * display.
	 * 
	 * @param collectionName
	 *            - name of the collection (or sub-collection) to check inactive
	 *            record display setting
	 * @return true if inactive records should be displayed, false otherwise
	 */
	@Deprecated
	public boolean getShowInactiveRecords(String collectionName);

	/**
	 * Returns the Map used to control the state of inactive record collection
	 * display. Exposed for setting from the maintenance jsp.
	 */
	@Deprecated
	public Map<String, Boolean> getInactiveRecordDisplay();

	/**
	 * Indicates to maintainble whether or not inactive records should be
	 * displayed for the given collection name.
	 * 
	 * @param collectionName
	 *            - name of the collection (or sub-collection) to set inactive
	 *            record display setting
	 * @param showInactive
	 *            - true to display inactive, false to not display inactive
	 *            records
	 */
	@Deprecated
	public void setShowInactiveRecords(String collectionName,
			boolean showInactive);
}
