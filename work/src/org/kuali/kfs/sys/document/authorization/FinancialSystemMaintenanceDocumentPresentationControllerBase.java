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
package org.kuali.kfs.sys.document.authorization;

import java.util.Set;

import org.kuali.kfs.coa.service.AccountPersistenceStructureService;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.parameter.ParameterEvaluatorService;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.document.authorization.MaintenanceDocumentPresentationControllerBase;
import org.kuali.rice.krad.bo.PersistableBusinessObject;

public class FinancialSystemMaintenanceDocumentPresentationControllerBase extends MaintenanceDocumentPresentationControllerBase {
    private static ParameterEvaluatorService parameterEvaluatorService;

    /**
     * the following three methods still accept the deprecated class as argument in order to bridge the gap between old and new maintenance API
     *
     * This is just workaround solution. The better solution would be to replace old API with new one.
     */
    @Override
    public Set<String> getConditionallyReadOnlyPropertyNames(MaintenanceDocument document) {
        Set<String> readOnlyPropertyNames = super.getConditionallyReadOnlyPropertyNames(document);

        // if accounts can't cross charts, then all chartOfAccountsCode fields shall be displayed readOnly
        if (!SpringContext.getBean(AccountService.class).accountsCanCrossCharts()) {
            AccountPersistenceStructureService apsService = SpringContext.getBean(AccountPersistenceStructureService.class);
            PersistableBusinessObject bo = document.getNewMaintainableObject().getBusinessObject();

            // non-collection reference accounts
            Set<String> coaCodeNames = apsService.listChartOfAccountsCodeNames(bo);
            readOnlyPropertyNames.addAll(coaCodeNames);

            // collection reference accounts
            coaCodeNames = apsService.listCollectionChartOfAccountsCodeNames(bo);
            readOnlyPropertyNames.addAll(coaCodeNames);
        }

        return readOnlyPropertyNames;
    }

    protected ParameterEvaluatorService getParameterEvaluatorService() {
        if (parameterEvaluatorService == null) {
            parameterEvaluatorService = SpringContext.getBean(ParameterEvaluatorService.class);
        }
        return parameterEvaluatorService;
    }

    
}
