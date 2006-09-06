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

import org.kuali.core.service.KualiConfigurationService;
import org.kuali.module.financial.bo.OffsetAccount;
import org.kuali.module.gl.bo.OriginEntry;

/**
 * This interface defines methods that a FlexibleOffsetAccount Service must provide.
 * 
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public interface FlexibleOffsetAccountService {

    /**
     * Retrieves the OffsetAccount by its composite primary key (all passed in as parameters) if the SYSTEM parameter
     * FLEXIBLE_OFFSET_ENABLED_FLAG is true.
     * 
     * @param chartOfAccountsCode
     * @param accountNumber
     * @param financialOffsetObjectCode
     * @return An OffsetAccount object instance. Returns null if there is none with the given key, or if the SYSTEM parameter
     *         FLEXIBLE_OFFSET_ENABLED_FLAG is false.
     */
    public OffsetAccount getByPrimaryIdIfEnabled(String chartOfAccountsCode, String accountNumber, String financialOffsetObjectCode);

    /**
     * Retrieves whether the SYSTEM parameter FLEXIBLE_OFFSET_ENABLED_FLAG is true.
     * 
     * @return whether the SYSTEM parameter FLEXIBLE_OFFSET_ENABLED_FLAG is true.
     */
    public boolean getEnabled();

    /**
     * This method will apply the flexible offset account if necessary. It will only change the chart, account, sub account and sub
     * object on the transaction. If the flexible offset isn't enabled or valid for this transaction, it will be unchanged.
     * 
     * It throws a FlexibleOffset
     * 
     * @param transaction
     * @return true if transaction was changed, false if not
     */
    public boolean updateOffset(OriginEntry transaction);

    // The methods below are completely unrelated to flexible offset and shouldn't be in the interface. They expose the
    // implementation to the outside world.

    /**
     * Sets the configuration service that this service will use. This method is in the interface for tests to use mock objects.
     * Callers should keep in mind that Spring services are singletons, so changes to one are global.
     * 
     * @param kualiConfigurationService
     */
    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService);

    /**
     * Gets the configuration service that this service uses. This method is in the interface for tests to restore after using mock
     * objects.
     */
    public KualiConfigurationService getKualiConfigurationService();
}
