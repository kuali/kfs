/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
