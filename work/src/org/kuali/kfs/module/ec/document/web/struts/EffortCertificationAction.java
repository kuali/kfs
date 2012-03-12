/*
 * Copyright 2005-2008 The Kuali Foundation
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
