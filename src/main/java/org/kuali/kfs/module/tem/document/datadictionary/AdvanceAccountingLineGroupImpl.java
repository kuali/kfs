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
package org.kuali.kfs.module.tem.document.datadictionary;

import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.sys.document.web.DefaultAccountingLineGroupImpl;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Accounting Line Group implementation with special logic to help with advance accounting lines
 */
public class AdvanceAccountingLineGroupImpl extends DefaultAccountingLineGroupImpl {

    /**
     * Overridden to show totals when advance accounting lines are present
     * @see org.kuali.kfs.sys.document.web.DefaultAccountingLineGroupImpl#shouldRenderTotals()
     */
    @Override
    public boolean shouldRenderTotals() {
        boolean renderTotals = !accountingDocument.getSourceAccountingLines().isEmpty() || !accountingDocument.getTargetAccountingLines().isEmpty();
        if (accountingDocument instanceof TravelAuthorizationDocument) {
            final TravelAuthorizationDocument authorization = (TravelAuthorizationDocument)accountingDocument;
            renderTotals = renderTotals || (!ObjectUtils.isNull(authorization.getAdvanceAccountingLines()) && !authorization.getAdvanceAccountingLines().isEmpty());
        }
        renderTotals &= groupDefinition.getTotals() != null && groupDefinition.getTotals().size() > 0;
        return renderTotals;
    }

}
