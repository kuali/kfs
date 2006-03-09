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

import org.kuali.module.financial.bo.CashDrawer;


/**
 * This interface defines methods that a CashDrawer service implementation must provide.
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public interface CashDrawerService {
    /**
     * Closes the CashDrawer instance associated with the given workgroupName, creating one if necessary
     */
    public void closeCashDrawer(String workgroupName);

    /**
     * Opens the CashDrawer instance associated with the given workgroupName, creating one if necessary
     */
    public void openCashDrawer(String workgroupName);

    /**
     * Retrieves the CashDrawer instance associated with the given workgroupName, if any.
     * 
     * @param workgroupName
     * @return CashDrawer instance or null
     */
    public CashDrawer getByWorkgroupName(String workgroupName);

    /**
     * Saves the given CashDrawer instance to the DB.
     * 
     * @param cashDrawer
     * @return the CashDrawer that was just saved
     */
    public CashDrawer save(CashDrawer cashDrawer);

    /**
     * Delete the given CashDrawer instance from the DB.
     * 
     * @param line
     */
    public void delete(CashDrawer cashDrawer);
}