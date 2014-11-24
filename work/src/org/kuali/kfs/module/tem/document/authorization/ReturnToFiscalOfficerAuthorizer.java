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
package org.kuali.kfs.module.tem.document.authorization;

import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.rice.kim.api.identity.Person;

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
