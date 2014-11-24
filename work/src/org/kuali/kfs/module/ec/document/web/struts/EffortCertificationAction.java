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
package org.kuali.kfs.module.ec.document.web.struts;

import java.util.List;

import org.kuali.kfs.module.ec.businessobject.EffortCertificationDetail;
import org.kuali.kfs.module.ec.businessobject.EffortCertificationDetailLineOverride;
import org.kuali.kfs.sys.businessobject.AccountingLineOverride;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentActionBase;
import org.kuali.rice.krad.rules.rule.event.KualiDocumentEvent;
import org.kuali.rice.krad.service.KualiRuleService;
import org.kuali.rice.krad.service.PersistenceService;

/**
 * To define Actions for EffortCertification document.
 */
public class EffortCertificationAction extends FinancialSystemTransactionalDocumentActionBase {

    /**
     * execute the rules associated with the given event
     * 
     * @param event the event that just occured
     * @return true if the rules associated with the given event pass; otherwise, false
     */
    protected boolean invokeRules(KualiDocumentEvent event) {
        return SpringContext.getBean(KualiRuleService.class).applyRules(event);
    }

    /**
     * Processes detail line overrides for output to JSP
     */
    protected void processDetailLineOverrides(List<EffortCertificationDetail> detailLines) {
        if (!detailLines.isEmpty()) {
            SpringContext.getBean(PersistenceService.class).retrieveReferenceObjects(detailLines, EffortCertificationDetailLineOverride.REFRESH_FIELDS);

            for (EffortCertificationDetail detailLine : detailLines) {
                EffortCertificationDetailLineOverride.processForOutput(detailLine);
            }
        }
    }

    /**
     * For the given detail line, set the corresponding override code
     * 
     * @param line the given detail line
     */
    protected void updateDetailLineOverrideCode(EffortCertificationDetail detailLine) {
        AccountingLineOverride override = EffortCertificationDetailLineOverride.determineNeededOverrides(detailLine);
        detailLine.setOverrideCode(override.getCode());
    }
}
