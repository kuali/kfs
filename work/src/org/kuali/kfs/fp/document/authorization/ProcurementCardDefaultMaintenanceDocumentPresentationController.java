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
package org.kuali.kfs.fp.document.authorization;

import java.util.Set;

import org.kuali.kfs.fp.batch.ProcurementCardCreateDocumentsStep;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentPresentationControllerBase;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.bo.BusinessObject;

/**
 * Presentation controller for the ProcurementCardDefault Maintenance Document
 */
public class ProcurementCardDefaultMaintenanceDocumentPresentationController extends FinancialSystemMaintenanceDocumentPresentationControllerBase {
    protected static ParameterService parameterService;

    protected final static String PROCUREMENT_CARD_DEFAULT_HOLDER_SECTION_ID = "KFS-FP_ProcurementCardDefault-EditProcurementCardHolder";
    protected final static String PROCUREMENT_CARD_DEFAULT_ACCOUNTING_SECTION_ID = "KFS-FP_ProcurementCardDefault-EditProcurementCardAccounting";

    /**
     * Displays or hides the KFS-FP_ProcurementCardDefault-EditProcurementCardHolder and KFS-FP_ProcurementCardDefault-EditProcurementCardAccounting
     * sections based on if parameters for use are turned on
     * @see org.kuali.rice.kns.document.authorization.MaintenanceDocumentPresentationControllerBase#getConditionallyHiddenSectionIds(org.kuali.rice.kns.bo.BusinessObject)
     */
    @Override
    public Set<String> getConditionallyHiddenSectionIds(BusinessObject businessObject) {
        Set<String> sectionIds = super.getConditionallyHiddenSectionIds(businessObject);
        final boolean cardHolderDefaultTurnedOn = isCardHolderDefaultTurnedOn();
        if (!cardHolderDefaultTurnedOn) {
            sectionIds.add(PROCUREMENT_CARD_DEFAULT_HOLDER_SECTION_ID);
        }
        if (!cardHolderDefaultTurnedOn && !isAccountDefaultTurnedOn()) {
            sectionIds.add(PROCUREMENT_CARD_DEFAULT_ACCOUNTING_SECTION_ID);
        }
        return sectionIds;
    }

    /**
     * @return true if use of card holder defaults is turned on via parameter, false if it is turned off
     */
    protected boolean isCardHolderDefaultTurnedOn() {
        return getParameterService().getParameterValueAsBoolean(ProcurementCardCreateDocumentsStep.class, ProcurementCardCreateDocumentsStep.USE_CARD_HOLDER_DEFAULT_PARAMETER_NAME);
    }

    /**
     * @return true if use of accounting defaults is turned on via parameter, false if it is turned off
     */
    protected boolean isAccountDefaultTurnedOn() {
        return getParameterService().getParameterValueAsBoolean(ProcurementCardCreateDocumentsStep.class, ProcurementCardCreateDocumentsStep.USE_ACCOUNTING_DEFAULT_PARAMETER_NAME);
    }

    /**
     * @return the default implementation of the ParameterService
     */
    @Override
    protected synchronized ParameterService getParameterService() {
        if (parameterService == null) {
            parameterService = SpringContext.getBean(ParameterService.class);
        }
        return parameterService;
    }
}
