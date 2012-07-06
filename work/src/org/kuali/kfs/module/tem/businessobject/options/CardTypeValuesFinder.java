/*
 * Copyright 2010 The Kuali Foundation.
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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.businessobject.AccommodationType;
import org.kuali.kfs.module.tem.businessobject.ImportedExpense;
import org.kuali.kfs.module.tem.document.web.struts.TravelFormBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.util.KeyLabelPair;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.GlobalVariables;

public class CardTypeValuesFinder extends KeyValuesBase {


    @Override
    public List getKeyValues() {
        List keyValues = new ArrayList();

        List<ImportedExpense> importedExpenses = ((TravelFormBase)GlobalVariables.getKualiForm()).getTravelDocument().getImportedExpenses();
        Map<String,String> map = new HashMap<String, String>();
        keyValues.add(new KeyLabelPair(TemConstants.NOT_APPLICABLE, TemConstants.NOT_APPLICABLE));
        for (ImportedExpense expense : importedExpenses) {
            String cardType = expense.getCardType();
            if (cardType != null && !map.containsKey(cardType)){
                keyValues.add(new KeyLabelPair(cardType,cardType));
                map.put(cardType, cardType);
            }
        }

        return keyValues;
    }

}
