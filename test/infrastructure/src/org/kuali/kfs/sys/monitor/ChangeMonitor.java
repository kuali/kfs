/*
 * Copyright 2005 The Kuali Foundation
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
package org.kuali.kfs.sys.monitor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Simplifies writing tests which have to iterate in place until something happens (or until some timeout occurs and lets the test
 * fail).
 */

abstract public class ChangeMonitor {
    private static final Log LOG = LogFactory.getLog(ChangeMonitor.class);

    /**
     * Iterates, with pauseSeconds seconds between iterations, until either the given ChangeMonitor's valueChanged method returns
     * true, or at least maxWaitSeconds seconds have passed.
     * 
     * @param monitor ChangeMonitor instance which watches for whatever change your test is waiting for
     * @param maxWaitSeconds
     * @param pauseSeconds
     * @return true if the the ChangeMonitor's valueChanged method returned true before time ran out
     */
    public static boolean waitUntilChange(ChangeMonitor monitor, int maxWaitSeconds, int pauseSeconds) throws Exception {
        long maxWaitMs = maxWaitSeconds * 1000;
        long pauseMs = pauseSeconds * 1000;

        boolean valueChanged = false;
        boolean interrupted = false;
        long startTimeMs = System.currentTimeMillis();
        long endTimeMs = startTimeMs + maxWaitMs;

        Thread.sleep(pauseMs / 10); // the first time through, sleep a fraction of the specified time
        valueChanged = monitor.valueChanged();
        LOG.debug("starting wait loop");
        while (!interrupted && !valueChanged && (System.currentTimeMillis() < endTimeMs)) {
            try {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("sleeping for " + pauseMs + " ms");
                }
                Thread.sleep(pauseMs);
            }
            catch (InterruptedException e) {
                interrupted = true;
            }
            LOG.debug("checking wait loop sentinel");
            valueChanged = monitor.valueChanged();
        }
        LOG.debug("finished wait loop (" + valueChanged + ")");

        return valueChanged;
    }


    /**
     * @return true if the value being monitored has changed
     */
    abstract public boolean valueChanged() throws Exception;
}
