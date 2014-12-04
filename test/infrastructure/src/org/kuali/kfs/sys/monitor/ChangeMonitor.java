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
