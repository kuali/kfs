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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.businessobject.DisbursementPayee;
import org.kuali.kfs.fp.businessobject.lookup.DisbursementPayeeLookupableHelperServiceImpl;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.coreservice.framework.CoreFrameworkServiceLocator;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kim.impl.KIMPropertyConstants;
import org.kuali.rice.krad.util.ObjectUtils;

import edu.arizona.kfs.sys.UaKFSConstants;

public class UaDisbursementPayeeLookupableHelperServiceImpl extends DisbursementPayeeLookupableHelperServiceImpl {
	private static final long serialVersionUID = 1L;

	private static volatile PersonService personService;

	private PersonService getPersonService() {
		if (personService == null) {
			personService = SpringContext.getBean(PersonService.class);
		}
		return personService;
	}


	/**
	 * @see org.kuali.kfs.fp.businessobject.lookup.DisbursementPayeeLookupableHelperServiceImpl#getPersonAsPayees(java.util.Map)
	 */
	protected List<DisbursementPayee> getPersonAsPayees(Map<String, String> fieldValues) {
		List<DisbursementPayee> payeeList = new ArrayList<DisbursementPayee>();

		ArrayList<String> employeeStatusCodes = new ArrayList<String>(CoreFrameworkServiceLocator.getParameterService().getParameterValuesAsString(DisbursementVoucherDocument.class, UaKFSConstants.ACTIVE_EMPLOYEE_STATUS_CODES_PARAM_NAME));
		if (ObjectUtils.isNull(employeeStatusCodes) || employeeStatusCodes.isEmpty()) {
			throw new RuntimeException("The ACTIVE_EMPLOYEE_STATUS_CODES parameter has no values.");
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

		Map<String, String> personFieldValues = this.getPersonFieldValues(fieldValues);
		personFieldValues.put(KIMPropertyConstants.Person.EMPLOYEE_STATUS_CODE, employeeStatusCode);
		List<? extends Person> persons = getPersonService().findPeople(personFieldValues);

		for (Person personDetail : persons) {
			DisbursementPayee payee = getPayeeFromPerson(personDetail, fieldValues);
			payeeList.add(payee);
		}

		return payeeList;
	}

	@Override
	protected Map<String, String> getPersonFieldValues(Map<String, String> fieldValues) {
		Map<String, String> personFieldValues = new HashMap<String, String>();
		String firstName = fieldValues.get(KFSPropertyConstants.PERSON_FIRST_NAME);
		String lastName = fieldValues.get(KFSPropertyConstants.PERSON_LAST_NAME);
		String employeeID = fieldValues.get(KFSPropertyConstants.EMPLOYEE_ID);
		String active = fieldValues.get(KFSPropertyConstants.ACTIVE);
		if (StringUtils.isNotBlank(firstName)) {
			personFieldValues.put(KFSPropertyConstants.PERSON_FIRST_NAME, firstName);
		}
		if (StringUtils.isNotBlank(lastName)) {
			personFieldValues.put(KFSPropertyConstants.PERSON_LAST_NAME, lastName);
		}
		if (StringUtils.isNotBlank(employeeID)) {
			personFieldValues.put(KFSPropertyConstants.EMPLOYEE_ID, employeeID);
		}
		if (StringUtils.isNotBlank(active)) {
			personFieldValues.put(KFSPropertyConstants.ACTIVE, active);
		}
		return personFieldValues;
	}

}
