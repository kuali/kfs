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
package org.kuali.rice.kim.impl.identity

import org.kuali.rice.kim.api.identity.Person
import org.kuali.rice.core.api.util.type.KualiDecimal

/**
 * Stub Person impl
 */
class TestPerson implements Person {
    String getPrincipalId() { return null }
    String getPrincipalName() { return null }
    String getEntityId() { return null }
    String getEntityTypeCode() { return null }
    String getFirstName() { return null }
    String getFirstNameUnmasked() { return null }
    String getMiddleName() { return null }
    String getMiddleNameUnmasked() { return null }
    String getLastName() { return null }
    String getLastNameUnmasked() { return null }
    String getName() { return null }
    String getNameUnmasked() { return null }
    String getEmailAddress() { return null }
    String getEmailAddressUnmasked() { return null }
    String getAddressLine1() { return null }
    String getAddressLine1Unmasked() { return null }
    String getAddressLine2() { return null }
    String getAddressLine2Unmasked() { return null }
    String getAddressLine3() { return null }
    String getAddressLine3Unmasked() { return null }
    String getAddressCity() { return null }
    String getAddressCityUnmasked() { return null }
    String getAddressStateProvinceCode() { return null }
    String getAddressStateProvinceCodeUnmasked() { return null }
    String getAddressPostalCode() { return null }
    String getAddressPostalCodeUnmasked() { return null }
    String getAddressCountryCode() { return null }
    String getAddressCountryCodeUnmasked() { return null }
    String getPhoneNumber() { return null }
    String getPhoneNumberUnmasked() { return null }
    String getCampusCode() { return null }
    Map<String, String> getExternalIdentifiers() { return null }
    boolean hasAffiliationOfType(String affiliationTypeCode) { return false }
    List<String> getCampusCodesForAffiliationOfType(String affiliationTypeCode) { return null }
    String getEmployeeStatusCode() { return null }
    String getEmployeeTypeCode() { return null }
    KualiDecimal getBaseSalaryAmount() { return null }
    String getExternalId(String externalIdentifierTypeCode) { return null }
    String getPrimaryDepartmentCode() { return null }
    String getEmployeeId() { return null }
    boolean isActive() { return false }
    void refresh() { }
}
