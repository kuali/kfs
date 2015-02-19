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

import org.kuali.kfs.module.ec.businessobject.EffortCertificationDetail;
import org.kuali.kfs.module.ec.document.EffortCertificationDocument;
import org.kuali.rice.krad.rules.rule.BusinessRule;

public interface CheckDetailLineAmountRule <E extends EffortCertificationDocument, D extends EffortCertificationDetail> extends BusinessRule {

    /**
     * validate the amounts on the given effort certification detail line before it can be processed
     * 
     * @param effortCertificationDocument the given effort certification document
     * @param effortCertificationDetail the given effort certification detail line
     * @return true if all rules are valid; otherwise, false
     */
    public boolean processCheckDetailLineAmountRules(E effortCertificationDocument, D effortCertificationDetail);
}
