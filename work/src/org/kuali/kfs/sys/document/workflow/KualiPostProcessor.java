/*
 * Copyright 2006-2007 The Kuali Foundation.
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

package org.kuali.kfs.sys.document.workflow;

import java.rmi.RemoteException;

import org.apache.log4j.Logger;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.dto.ActionTakenEventDTO;
import org.kuali.rice.kew.dto.AfterProcessEventDTO;
import org.kuali.rice.kew.dto.BeforeProcessEventDTO;
import org.kuali.rice.kew.dto.DeleteEventDTO;
import org.kuali.rice.kew.dto.DocumentRouteLevelChangeDTO;
import org.kuali.rice.kew.dto.DocumentRouteStatusChangeDTO;
import org.kuali.rice.kew.postprocessor.PostProcessorRemote;
import org.kuali.rice.kns.service.PostProcessorService;

/**
 * FIXME hjs: adding this class is only temporary to fix broken PURAP dependencies; will remove class once problems are fixed (see KFSMI-2395 and KULPURAP-3244)
 * 
 * This class is the public entry point by which workflow communicates status changes, level changes, and other useful changes. Note
 * that this class delegates all of these activities to the PostProcessorService, which does the actual work. This is done to ensure
 * proper transaction scoping, and to resolve some issues present otherwise. Because of this, its important to understand that a
 * transaction will be started at the PostProcessorService method call, so any work that needs to be done within the same
 * transaction needs to happen inside that service implementation, rather than in here.
 */
public class KualiPostProcessor implements PostProcessorRemote {

    private static Logger LOG = Logger.getLogger(KualiPostProcessor.class);

    /**
     * @see org.kuali.rice.kew.clientapp.PostProcessorRemote#doRouteStatusChange(org.kuali.rice.kew.clientapp.vo.DocumentRouteStatusChangeDTO)
     */
    public boolean doRouteStatusChange(DocumentRouteStatusChangeDTO statusChangeEvent) throws RemoteException {
        return SpringContext.getBean(PostProcessorService.class).doRouteStatusChange(statusChangeEvent);
    }

    /**
     * @see org.kuali.rice.kew.clientapp.PostProcessorRemote#doActionTaken(org.kuali.rice.kew.clientapp.vo.ActionTakenEventDTO)
     */
    public boolean doActionTaken(ActionTakenEventDTO event) throws RemoteException {
        return SpringContext.getBean(PostProcessorService.class).doActionTaken(event);
    }

    /**
     * @see org.kuali.rice.kew.clientapp.PostProcessorRemote#doDeleteRouteHeader(org.kuali.rice.kew.clientapp.vo.DeleteEventDTO)
     */
    public boolean doDeleteRouteHeader(DeleteEventDTO event) throws RemoteException {
        return SpringContext.getBean(PostProcessorService.class).doDeleteRouteHeader(event);
    }

    /**
     * @see org.kuali.rice.kew.clientapp.PostProcessorRemote#doRouteLevelChange(org.kuali.rice.kew.clientapp.vo.DocumentRouteLevelChangeDTO)
     */
    public boolean doRouteLevelChange(DocumentRouteLevelChangeDTO levelChangeEvent) throws RemoteException {
        return SpringContext.getBean(PostProcessorService.class).doRouteLevelChange(levelChangeEvent);
    }

    public boolean afterProcess(AfterProcessEventDTO arg0) throws Exception {
        return true;
    }

    public boolean beforeProcess(BeforeProcessEventDTO arg0) throws Exception {
        return true;
    }
}
