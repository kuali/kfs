/*
 * Copyright 2011 The Kuali Foundation.
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
package edu.arizona.kfs.fp.businessobject.lookup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.fp.businessobject.DisbursementPayee;
import org.kuali.kfs.fp.businessobject.lookup.DisbursementPayeeLookupableHelperServiceImpl;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.rice.coreservice.framework.CoreFrameworkServiceLocator;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.impl.KIMPropertyConstants;
import org.kuali.rice.krad.util.ObjectUtils;

import edu.arizona.kfs.sys.UaKFSConstants;

public class UaDisbursementPayeeLookupableHelperServiceImpl extends DisbursementPayeeLookupableHelperServiceImpl {
	private static final long serialVersionUID = 1L;

	/**
	 * @see org.kuali.kfs.fp.businessobject.lookup.DisbursementPayeeLookupableHelperServiceImpl#getPersonAsPayees(java.util.Map)
	 */
	protected List<DisbursementPayee> getPersonAsPayees(Map<String, String> fieldValues) {
		List<DisbursementPayee> payeeList = new ArrayList<DisbursementPayee>();

		List<String> employeeStatusCodes = (List<String>) CoreFrameworkServiceLocator.getParameterService().getParameterValuesAsString(DisbursementVoucherDocument.class, UaKFSConstants.ACTIVE_EMPLOYEE_STATUS_CODES_PARAM_NAME);
		if (ObjectUtils.isNull(employeeStatusCodes) || employeeStatusCodes.isEmpty()) {
			return super.getPersonAsPayees(fieldValues);
		}

		for (String statusCode : employeeStatusCodes) {
			List<DisbursementPayee> payeesByEmployeeStatusCode = this.getPersonAsPayeesByEmployeeStatusCode(fieldValues, statusCode);

			payeeList.addAll(payeesByEmployeeStatusCode);
		}

		return payeeList;
	}

	/**
	 * get a list of DV payees by the given employee status code
	 * 
	 * @param fieldValues
	 *            the given search criteria
	 * @param empoyeeStatusCode
	 *            the given employee status code
	 * @return a list of DV payees by the given employee status code
	 */
	protected List<DisbursementPayee> getPersonAsPayeesByEmployeeStatusCode(Map<String, String> fieldValues, String employeeStatusCode) {
		List<DisbursementPayee> payeeList = new ArrayList<DisbursementPayee>();

		fieldValues.put(KIMPropertyConstants.Person.EMPLOYEE_STATUS_CODE, employeeStatusCode);
		List<? extends Person> persons = KimApiServiceLocator.getPersonService().findPeople(fieldValues);

		for (Person personDetail : persons) {
			DisbursementPayee payee = getPayeeFromPerson(personDetail, fieldValues);
			payeeList.add(payee);
		}

		return payeeList;
	}

}
