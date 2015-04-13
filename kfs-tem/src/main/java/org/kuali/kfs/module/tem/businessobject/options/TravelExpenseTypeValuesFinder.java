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
import org.kuali.kfs.sys.KFSConstants;
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
        keyValues.add(new ConcreteKeyValue(KFSConstants.EMPTY_STRING, KFSConstants.EMPTY_STRING));

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
