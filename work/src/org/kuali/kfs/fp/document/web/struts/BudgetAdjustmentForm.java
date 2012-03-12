/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.fp.document.web.struts;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.AccountingLineOverride;
import org.kuali.kfs.sys.businessobject.AccountingLineOverride.COMPONENT;
import org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase;

/**
 * This class is the form class for the ProcurementCard document. This method extends the parent KualiTransactionalDocumentFormBase
 * class which contains all of the common form methods and form attributes needed by the Procurment Card document.
 */
public class BudgetAdjustmentForm extends KualiAccountingDocumentFormBase {

    protected static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetAdjustmentForm.class);

    
    /**
     * Constructs a BudgetAdjustmentForm instance and sets up the appropriately casted document. Also, the
     * newSourceLine/newTargetLine need to be the extended
     * BudgetAdjustmentSourceAccountingLine/BudgetAdjustmentTargetAccountingLine.
     */
    public BudgetAdjustmentForm() {
        super();
    }

    @Override
    protected String getDefaultDocumentTypeName() {
        return "BA";
    }
    
    @Override
    protected void repopulateOverrides(AccountingLine line, String accountingLinePropertyName, Map parameterMap) {
        determineNeededOverrides(line);
        if (line.getAccountExpiredOverrideNeeded()) {
            if (LOG.isDebugEnabled()) {
                StringUtils.join(parameterMap.keySet(), "\n");
            }
            if (parameterMap.containsKey(accountingLinePropertyName+".accountExpiredOverride.present")) {
                line.setAccountExpiredOverride(parameterMap.containsKey(accountingLinePropertyName+".accountExpiredOverride"));
            }
        } else {
            line.setAccountExpiredOverride(false);
        }
    }
    
    /**
     * Determines what overrides the given line needs.
     * 
     * @param line
     * @return what overrides the given line needs.
     */
    public static AccountingLineOverride determineNeededOverrides(AccountingLine line) {
        Set neededOverrideComponents = new HashSet();
        if (AccountingLineOverride.needsExpiredAccountOverride(line.getAccount())) {
            neededOverrideComponents.add(COMPONENT.EXPIRED_ACCOUNT);
        }
       
        return AccountingLineOverride.valueOf(neededOverrideComponents);
    }
}
