/*
 * Copyright 2013 The Kuali Foundation.
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
