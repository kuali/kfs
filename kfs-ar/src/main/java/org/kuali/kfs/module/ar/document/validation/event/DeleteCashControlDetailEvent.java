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
package org.kuali.kfs.module.ar.document.validation.event;

import org.kuali.kfs.module.ar.businessobject.CashControlDetail;
import org.kuali.kfs.module.ar.document.validation.DeleteCashControlDetailRule;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.document.TransactionalDocument;
import org.kuali.rice.krad.rules.rule.BusinessRule;

public class DeleteCashControlDetailEvent extends CashControlDetailEventBase {

    public DeleteCashControlDetailEvent(String errorPathPrefix, Document document, CashControlDetail cashControlDetail) {
        super("Deleteing cash control detail to document " + getDocumentId(document), errorPathPrefix, document, cashControlDetail);
    }

    @SuppressWarnings("unchecked")
    public Class getRuleInterfaceClass() {
        return DeleteCashControlDetailRule.class;
    }

    @SuppressWarnings("unchecked")
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((DeleteCashControlDetailRule) rule).processDeleteCashControlDetailBusinessRules((TransactionalDocument) getDocument(), getCashControlDetail());
    }
}
