/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.rules;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.core.exceptions.ReferentialIntegrityException;
import org.kuali.core.rules.LedgerPostingDocumentRuleBase;
import org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.OffsetDefinition;
import org.kuali.module.chart.service.OffsetDefinitionService;
import org.kuali.module.financial.bo.OffsetAccount;
import org.kuali.module.financial.service.FlexibleOffsetAccountService;
import org.kuali.module.gl.service.SufficientFundsService;

/**
 * This class contains a helper method used to implement a rule for the CashManagementDocument (a FinancialDocument) as well as to
 * implement rules for TransactionalDocuments.
 */
public class GeneralLedgerPostingDocumentRuleBase extends LedgerPostingDocumentRuleBase {
    /**
     * Logger for this class
     */
    private static final Logger LOG = Logger.getLogger(GeneralLedgerPostingDocumentRuleBase.class);

    
}
