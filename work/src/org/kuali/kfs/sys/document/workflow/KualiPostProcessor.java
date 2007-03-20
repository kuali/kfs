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

package org.kuali.workflow.postprocessor;

import java.rmi.RemoteException;

import org.apache.log4j.Logger;
import org.kuali.rice.KNSServiceLocator;

import edu.iu.uis.eden.clientapp.PostProcessorRemote;
import edu.iu.uis.eden.clientapp.vo.ActionTakenEventVO;
import edu.iu.uis.eden.clientapp.vo.DeleteEventVO;
import edu.iu.uis.eden.clientapp.vo.DocumentRouteLevelChangeVO;
import edu.iu.uis.eden.clientapp.vo.DocumentRouteStatusChangeVO;

/**
 * 
 * This class is the public entry point by which workflow communicates status changes, 
 * level changes, and other useful changes.
 * 
 * Note that this class delegates all of these activities to the PostProcessorService, 
 * which does the actual work.  This is done to ensure proper transaction scoping, and 
 * to resolve some issues present otherwise.
 * 
 * Because of this, its important to understand that a transaction will be started at 
 * the PostProcessorService method call, so any work that needs to be done within the 
 * same transaction needs to happen inside that service implementation, rather than 
 * in here.
 * 
 */
public class KualiPostProcessor implements PostProcessorRemote {

    private static Logger LOG = Logger.getLogger(KualiPostProcessor.class);

    /**
     * 
     * @see edu.iu.uis.eden.clientapp.PostProcessorRemote#doRouteStatusChange(edu.iu.uis.eden.clientapp.vo.DocumentRouteStatusChangeVO)
     */
    public boolean doRouteStatusChange(DocumentRouteStatusChangeVO statusChangeEvent) throws RemoteException {
        return KNSServiceLocator.getPostProcessorService().doRouteStatusChange(statusChangeEvent);
    }

    /**
     * 
     * @see edu.iu.uis.eden.clientapp.PostProcessorRemote#doActionTaken(edu.iu.uis.eden.clientapp.vo.ActionTakenEventVO)
     */
    public boolean doActionTaken(ActionTakenEventVO event) throws RemoteException {
        return KNSServiceLocator.getPostProcessorService().doActionTaken(event);
    }

    /**
     * 
     * @see edu.iu.uis.eden.clientapp.PostProcessorRemote#doDeleteRouteHeader(edu.iu.uis.eden.clientapp.vo.DeleteEventVO)
     */
    public boolean doDeleteRouteHeader(DeleteEventVO event) throws RemoteException {
        return KNSServiceLocator.getPostProcessorService().doDeleteRouteHeader(event);
    }

    /**
     * 
     * @see edu.iu.uis.eden.clientapp.PostProcessorRemote#doRouteLevelChange(edu.iu.uis.eden.clientapp.vo.DocumentRouteLevelChangeVO)
     */
    public boolean doRouteLevelChange(DocumentRouteLevelChangeVO levelChangeEvent) throws RemoteException {
        return KNSServiceLocator.getPostProcessorService().doRouteLevelChange(levelChangeEvent);
    }

 }