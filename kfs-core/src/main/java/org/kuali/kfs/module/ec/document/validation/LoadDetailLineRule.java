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
package org.kuali.kfs.module.ec.document.validation;

import org.kuali.kfs.module.ec.document.EffortCertificationDocument;
import org.kuali.rice.krad.rules.rule.BusinessRule;

public interface LoadDetailLineRule<E extends EffortCertificationDocument> extends BusinessRule {

    /**
     * validate the given effort certification document before a set of detail lines can be added into the given document
     * 
     * @param effortCertificationDocument the given effort certification document
     * @return true if all rules are valid; otherwise, false;
     */
    public boolean processLoadDetailLineRules(E effortCertificationDocument);
}
