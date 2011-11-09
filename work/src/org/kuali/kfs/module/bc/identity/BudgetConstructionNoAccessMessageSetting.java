/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.module.bc.identity;

import org.kuali.kfs.module.bc.document.BudgetConstructionDocument;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.util.MessageMap;

/**
 * Interface for role types services that can set no access messages for budget construction.
 */
public interface BudgetConstructionNoAccessMessageSetting {

    /**
     * Assumes the given user does not have view access for the given document and adds a message to given MessageList to indicate
     * why the user does not.
     * 
     * @param document Budget document that access was requested for
     * @param user Person who requested access
     * @param messageMap MessageMap for adding message to
     */
    public void setNoAccessMessage(BudgetConstructionDocument document, Person user, MessageMap messageMap);
}
