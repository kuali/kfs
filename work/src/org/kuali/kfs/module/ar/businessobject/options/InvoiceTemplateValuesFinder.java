/*
 * Copyright 2011 The Kuali Foundation.
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