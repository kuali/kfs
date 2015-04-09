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
