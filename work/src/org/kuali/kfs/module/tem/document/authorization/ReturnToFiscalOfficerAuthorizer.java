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
package org.kuali.kfs.module.tem.document.authorization;

import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.rice.kim.bo.Person;

/**
 * Supplies abstract method to define whether an authorizer can generically check for return to fiscal officer permissions
 * on a given {@link TravelDocument}
 * 
 */
public interface ReturnToFiscalOfficerAuthorizer {
    /**
     * Return the document to Fiscal Officer
     * 
     * @param document
     * @param user
     * @return true if <code>user</code> is authorized to return the <code>document</code> to the fiscal officer
     */
    boolean canReturnToFisicalOfficer(TravelDocument document, Person user);
}
