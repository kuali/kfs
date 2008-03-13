/*
 * Copyright 2005-2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.effort.web.struts.action;

import java.util.List;

import org.kuali.core.rule.event.KualiDocumentEvent;
import org.kuali.core.service.KualiRuleService;
import org.kuali.core.service.PersistenceService;
import org.kuali.core.web.struts.action.KualiTransactionalDocumentActionBase;
import org.kuali.kfs.bo.AccountingLineOverride;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.effort.bo.EffortCertificationDetail;
import org.kuali.module.effort.bo.EffortCertificationDetailLineOverride;

/**
 * To define Actions for EffortCertification document.
 */
public class EffortCertificationAction extends KualiTransactionalDocumentActionBase {

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