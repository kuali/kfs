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

package org.kuali.kfs.sys.document.workflow;

import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.action.ActionType;
import org.kuali.rice.kew.framework.postprocessor.ActionTakenEvent;
import org.kuali.rice.kew.framework.postprocessor.AfterProcessEvent;
import org.kuali.rice.kew.framework.postprocessor.BeforeProcessEvent;
import org.kuali.rice.kew.framework.postprocessor.DeleteEvent;
import org.kuali.rice.kew.framework.postprocessor.DocumentLockingEvent;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteLevelChange;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;
import org.kuali.rice.kew.framework.postprocessor.ProcessDocReport;
import org.kuali.rice.krad.service.PostProcessorService;

/**
 * This class is the public entry point by which workflow communicates status changes, level changes, and other useful changes. Note
 * that this class delegates all of these activities to the PostProcessorService, which does the actual work. This is done to ensure
 * proper transaction scoping, and to resolve some issues present otherwise. Because of this, its important to understand that a
 * transaction will be started at the PostProcessorService method call, so any work that needs to be done within the same
 * transaction needs to happen inside that service implementation, rather than in here.
 */
public class PostProcessor implements org.kuali.rice.kew.framework.postprocessor.PostProcessor {

    private static Logger LOG = Logger.getLogger(PostProcessor.class);

    public List<String> getDocumentIdsToLock(DocumentLockingEvent arg0) throws Exception {
        return SpringContext.getBean(PostProcessorService.class).getDocumentIdsToLock(arg0);
    }

    /**
     * @see org.kuali.rice.kew.clientapp.PostProcessorRemote#doRouteStatusChange(org.kuali.rice.kew.clientapp.vo.DocumentRouteStatusChange)
     */
    public ProcessDocReport doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) throws Exception {
        return SpringContext.getBean(PostProcessorService.class).doRouteStatusChange(statusChangeEvent);
    }

    /**
     * @see org.kuali.rice.kew.clientapp.PostProcessorRemote#doActionTaken(org.kuali.rice.kew.clientapp.vo.ActionTakenEventDTO)
     */
    public ProcessDocReport doActionTaken(ActionTakenEvent event) throws Exception {
        return SpringContext.getBean(PostProcessorService.class).doActionTaken(event);
    }

    /**
     * @see org.kuali.rice.kew.clientapp.PostProcessorRemote#doDeleteRouteHeader(org.kuali.rice.kew.clientapp.vo.DeleteEventDTO)
     */
    public ProcessDocReport doDeleteRouteHeader(DeleteEvent event) throws Exception {
        return SpringContext.getBean(PostProcessorService.class).doDeleteRouteHeader(event);
    }

    /**
     * @see org.kuali.rice.kew.clientapp.PostProcessorRemote#doRouteLevelChange(org.kuali.rice.kew.clientapp.vo.DocumentRouteLevelChangeDTO)
     */
    public ProcessDocReport doRouteLevelChange(DocumentRouteLevelChange levelChangeEvent) throws Exception {
        return SpringContext.getBean(PostProcessorService.class).doRouteLevelChange(levelChangeEvent);
    }

    public ProcessDocReport afterProcess(AfterProcessEvent arg0) throws Exception {
        return new ProcessDocReport(true);
    }

    public ProcessDocReport beforeProcess(BeforeProcessEvent arg0) throws Exception {
        return new ProcessDocReport(true);
    }

    @Override
    public ProcessDocReport afterActionTaken(ActionType performed, ActionTakenEvent event) throws Exception {
        return SpringContext.getBean(PostProcessorService.class).afterActionTaken(performed, event);
    }
}
