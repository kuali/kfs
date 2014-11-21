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
package org.kuali.kfs.module.purap.document;

import java.util.Map;

import org.kuali.kfs.module.purap.businessobject.PurchaseOrderContractLanguage;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.kns.document.MaintenanceDocument;

/**
 * A special implementation of Maintainable specifically for PurchaseOrderContractLanguage
 * maintenance page to override the behavior when the PurchaseOrderContractLanguage 
 * maintenance document is copied.
 */
public class PurchaseOrderContractLanguageMaintainableImpl extends FinancialSystemMaintainable {

    /**
     * Overrides the method in KualiMaintainableImpl to set the contract language create date
     * to current date.
     * 
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#processAfterCopy()
     */
    @Override
    public void processAfterCopy( MaintenanceDocument document, Map<String,String[]> parameters ) {
        PurchaseOrderContractLanguage pocl = (PurchaseOrderContractLanguage) super.getBusinessObject();
        pocl.setContractLanguageCreateDate(SpringContext.getBean(DateTimeService.class).getCurrentSqlDate());
        super.processAfterCopy(document, parameters);
    }

}
