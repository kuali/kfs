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
package org.kuali.module.financial.service;

import org.kuali.core.util.KualiDecimal;
import org.kuali.module.financial.bo.CashDrawer;


/**
 * This interface defines methods that a CashDrawer service implementation must provide.
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public interface CashDrawerService {
    /**
     * Closes the CashDrawer instance associated with the given workgroupName, creating one if necessary.
     */
    public void closeCashDrawer(String workgroupName);

    /**
     * Opens the CashDrawer instance associated with the given workgroupName, creating one if necessary. Records the given
     * documentId as the document which opened the cashdrawer.
     */
    public void openCashDrawer(String workgroupName, String documentId);

    /**
     * Locks the currently-open CashDrawer instance associated with the given workgroupName, throwing an IllegalStateException if
     * that cashDrawer is not open (i.e. is closed or locked). Records the given documentId as the document which locked the
     * cashDrawer.
     */
    public void lockCashDrawer(String workgroupName, String documentId);

    /**
     * Unlocks the currently-locked CashDrawer instance associated with the given workgroupName, throwing an IllegalStateException
     * if that cashDrawer is not locked (i.e. is closed or open). Records the given documentId as the document which unlocked the
     * cashDrawer.
     */
    public void unlockCashDrawer(String workgroupName, String documentId);


    /**
     * Retrieves the CashDrawer instance associated with the given workgroupName, if any. If autocreate is true, and no CashDrawer
     * for the given workgroupName exists, getByWorkgroupName will return a newly-created (non-persisted) CashDrawer instance.
     * 
     * @param workgroupName
     * @param autocreate
     * @return CashDrawer instance or null
     */
    public CashDrawer getByWorkgroupName(String workgroupName, boolean autocreate);
}