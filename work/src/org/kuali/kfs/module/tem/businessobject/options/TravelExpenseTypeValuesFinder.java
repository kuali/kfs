/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.businessobject.ExpenseType;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.document.web.struts.TravelFormBase;
import org.kuali.kfs.module.tem.service.TravelExpenseService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.kns.web.struts.form.KualiForm;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.util.ObjectUtils;

@SuppressWarnings("deprecation")
public class TravelExpenseTypeValuesFinder extends KeyValuesBase {
    /**
     * @see org.kuali.rice.krad.keyvalues.KeyValuesFinder#getKeyValues()
     */
    @Override
    public List<KeyValue> getKeyValues() {
        List<KeyValue> keyValues = new ArrayList<KeyValue>();
        keyValues.add(new ConcreteKeyValue("", ""));

        final KualiForm kualiForm = KNSGlobalVariables.getKualiForm();
        String documentType = TemConstants.TravelDocTypes.TEM_TRANSACTIONAL_DOCUMENT;
        String tripType = null;
        String travelerType = null;
        boolean groupOnly = false;
        if (kualiForm != null && kualiForm instanceof TravelFormBase) {
            final TravelFormBase travelDocForm = (TravelFormBase)kualiForm;
            final TravelDocument travelDocument = travelDocForm.getTravelDocument();

            documentType = SpringContext.getBean(TravelDocumentService.class).getDocumentType(travelDocument);

            if (!StringUtils.isBlank(travelDocument.getTripTypeCode())) {
                tripType = travelDocument.getTripTypeCode();
            }
            if (!ObjectUtils.isNull(travelDocument.getTraveler()) && !StringUtils.isBlank(travelDocument.getTraveler().getTravelerTypeCode())) {
                travelerType = travelDocument.getTraveler().getTravelerTypeCode();
            }
        }

        final List<ExpenseType> expenseTypes = SpringContext.getBean(TravelExpenseService.class).getExpenseTypesForDocument(documentType, tripType, travelerType, groupOnly);
        for (ExpenseType expenseType : expenseTypes) {
            keyValues.add(new ConcreteKeyValue(expenseType.getCode(), expenseType.getCodeAndDescription()));
        }

        return keyValues;
    }

}
