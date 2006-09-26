/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.test.monitor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Simplifies writing tests which have to iterate in place until something happens (or until some timeout occurs and lets the test
 * fail).
 * 
 * @author Kuali Nervous System Team ()
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
