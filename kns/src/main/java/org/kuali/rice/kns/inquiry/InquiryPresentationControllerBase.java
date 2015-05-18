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

import java.util.HashSet;
import java.util.Set;

import org.kuali.rice.kns.inquiry.InquiryPresentationController;
import org.kuali.rice.krad.bo.BusinessObject;

public class InquiryPresentationControllerBase implements InquiryPresentationController {
	/**
	 * Implement this method to hide fields based on specific data in the record being inquired into
	 * 
	 * @return Set of property names that should be hidden
	 */
	public Set<String> getConditionallyHiddenPropertyNames(BusinessObject businessObject) {
		return new HashSet<String>();
	}

	/**
	 * Implement this method to hide sections based on specific data in the record being inquired into
	 * 
	 * @return Set of section ids that should be hidden
	 */
	public Set<String> getConditionallyHiddenSectionIds(BusinessObject businessObject) {
		return new HashSet<String>();
	}
}
