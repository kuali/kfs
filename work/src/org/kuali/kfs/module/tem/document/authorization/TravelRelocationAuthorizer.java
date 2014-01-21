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
package org.kuali.kfs.module.tem.document.authorization;

import org.kuali.kfs.module.tem.document.TravelRelocationDocument;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.util.ObjectUtils;


public class TravelRelocationAuthorizer extends TravelArrangeableAuthorizer implements ReturnToFiscalOfficerAuthorizer {

    /**
     *
     * @param relocation
     * @param user
     * @return
     */
    public boolean canCertify(final TravelRelocationDocument relocation, Person user) {
        if(ObjectUtils.isNull(relocation.getTraveler())) {
            return false;
        }
        else if (user.getPrincipalId().equals(relocation.getTraveler().getPrincipalId())
            || !isEmployee(relocation.getTraveler())) {
            return true;
        }

        return false;
    }
}
