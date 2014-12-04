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
package org.kuali.kfs.module.ar.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.ar.businessobject.InvoiceTemplate;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.module.ar.document.web.struts.ContractsGrantsInvoiceDocumentForm;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.kns.web.struts.form.KualiForm;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.service.KeyValuesService;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * ValuesFinder that returns a list of InvoiceTemplates.
 */
public class InvoiceTemplateValuesFinder extends KeyValuesBase {

    protected List<KeyValue> keyValues = new ArrayList();

    /**
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<KeyValue> getKeyValues() {
        final ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService = SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class);
        final Person currentUser = GlobalVariables.getUserSession().getPerson();

        List<InvoiceTemplate> boList = (List<InvoiceTemplate>) SpringContext.getBean(KeyValuesService.class).findAll(InvoiceTemplate.class);
        for (InvoiceTemplate element : boList) {
            if (element.isActive()) {
                if (!element.isRestrictUseByChartOrg()) {
                    keyValues.add(new ConcreteKeyValue(element.getInvoiceTemplateCode(), element.getInvoiceTemplateDescription()));
                }
                else {
                    ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument = null;
                    KualiForm kualiForm = KNSGlobalVariables.getKualiForm();
                    if (kualiForm instanceof ContractsGrantsInvoiceDocumentForm) {
                        contractsGrantsInvoiceDocument = ((ContractsGrantsInvoiceDocumentForm)kualiForm).getContractsGrantsInvoiceDocument();
                    }
                    if (contractsGrantsInvoiceDocumentService.isTemplateValidForContractsGrantsInvoiceDocument(element, contractsGrantsInvoiceDocument)) {
                        keyValues.add(new ConcreteKeyValue(element.getInvoiceTemplateCode(), element.getInvoiceTemplateDescription()));
                    }
                }
            }
        }
        return keyValues;
    }

    /**
     * @see org.kuali.rice.krad.keyvalues.KeyValuesBase#clearInternalCache()
     */
    @Override
    public void clearInternalCache() {
        keyValues = null;
    }

}
