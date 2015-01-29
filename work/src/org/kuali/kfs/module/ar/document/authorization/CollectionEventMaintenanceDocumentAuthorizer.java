/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2015 The Kuali Foundation
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
package org.kuali.kfs.module.ar.document.authorization;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.businessobject.CollectionEvent;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentAuthorizerBase;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Authorizer class for ResearchRiskTypeMaintenanceDocument - allows for targeted field disabling.
 */
public class CollectionEventMaintenanceDocumentAuthorizer extends FinancialSystemMaintenanceDocumentAuthorizerBase {

    @Override
    public boolean canMaintain(Object dataObject, Person user) {
        CollectionEvent collectionEvent = (CollectionEvent)dataObject;

        if (super.canMaintain(dataObject, user) && ObjectUtils.isNotNull(collectionEvent) &&
                StringUtils.isNotBlank(collectionEvent.getInvoiceNumber()) &&
                ObjectUtils.isNotNull(collectionEvent.getInvoiceDocument()) &&
                SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class).canViewInvoice(collectionEvent.getInvoiceDocument(), user.getPrincipalId())) {
            return true;
        } else {
            return false;
        }
    }

}
