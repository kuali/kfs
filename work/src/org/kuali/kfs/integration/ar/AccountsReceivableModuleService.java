/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.integration.ar;

import org.kuali.kfs.sys.service.ElectronicPaymentClaimingDocumentGenerationStrategy;

/**
 * Methods which allow core KFS modules to interact with the Accounts Receivable module.
 */
public interface AccountsReceivableModuleService {
    /**
     * A method that returns an implementation of the ElectronicPaymentClaimingDocumentGenerationStrategy interface
     * which will claim electronic payments for the Accounts Receivable module.
     * @return an appropriate implementation of ElectronicPaymentClaimingDocumentGenerationStrategy
     */
    public abstract ElectronicPaymentClaimingDocumentGenerationStrategy getAccountsReceivablePaymentClaimingStrategy(); 
}
