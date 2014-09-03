/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.cg.service;

import java.util.Map;

/**
 * Service with methods related to Contracts & Grants Lookups.
 */
public interface ContractsAndGrantsLookupService {

    /**
     * Attempt to find a Principal using the user name passed in. If found, put the principal Id in the search fields
     * and remove the dummy attribute (user name).
     *
     * @param fieldValues
     * @param userNameField
     * @param universalUserIdField
     * @return true if user name was empty or principal was found, false if no principal was found
     */
    public boolean setupSearchFields(Map<String, String> fieldValues, String userNameField, String universalUserIdField);

}
