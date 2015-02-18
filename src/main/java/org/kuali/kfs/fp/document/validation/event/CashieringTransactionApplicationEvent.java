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
package org.kuali.kfs.fp.document.validation.event;

import org.kuali.kfs.fp.businessobject.CashDrawer;
import org.kuali.kfs.fp.businessobject.CashieringTransaction;
import org.kuali.rice.krad.rules.rule.event.KualiDocumentEvent;

/**
 * An event generated when a cash management document is about to apply a cashiering transaction
 */
public interface CashieringTransactionApplicationEvent extends KualiDocumentEvent {
    
    /**
     * Returns the cashiering transaction to be validated
     * @return the cashiering transaction to be validated
     */
    public abstract CashieringTransaction getCashieringTransaction();
    
    /**
     * Returns the cash drawer that the cashiering transaction to validate will be applied to
     * @return the cash drawer's current state on the cash management document initiating the cashiering transaction
     */
    public abstract CashDrawer getCashDrawer();
}
