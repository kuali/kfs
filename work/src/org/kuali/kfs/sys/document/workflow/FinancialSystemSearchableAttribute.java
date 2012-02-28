/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.sys.document.workflow;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.core.api.uif.RemotableAttributeField;
import org.kuali.rice.kew.api.extension.ExtensionDefinition;
import org.kuali.rice.krad.workflow.attribute.DataDictionarySearchableAttribute;

//RICE20 This class needs to be fixed to support pre-rice2.0 features
public class FinancialSystemSearchableAttribute extends DataDictionarySearchableAttribute {

    @Override
    public List<RemotableAttributeField> getSearchFields(ExtensionDefinition extensionDefinition, String documentTypeName) {
        // TODO Auto-generated method stub
        List<RemotableAttributeField> fields = new ArrayList<RemotableAttributeField>( super.getSearchFields(extensionDefinition, documentTypeName) );
        for ( RemotableAttributeField field : fields ) {
            RemotableAttributeField.Builder newField = RemotableAttributeField.Builder.create(field);
//            newField.setAttributeLookupSettings( AttributeLookupSettings.Builder.)
//            newField.set
//            newField.getWidgets().iterator().next().build().
//            RemotableAbstractWidget
            // TODO: create widgets
//            RemotableQuickFinder
        }
        return fields;
    }


//    @Override
//    public List<RemotableAttributeField> getSearchFields(ExtensionDefinition extensionDefinition, String documentTypeName) {
//        List<Row> searchRows = getSearchingRows(documentTypeName);
//        List<RemotableAttributeField> attributeFields = new ArrayList<RemotableAttributeField>();
//        for (Row row : searchRows) {
//            List<RemotableAttributeField> rowAttributeFields = new ArrayList<RemotableAttributeField>();
//            for (Field field : row.getFields()) {
//                RemotableAttributeField remotableAttributeField = KRADServiceLocatorWeb.getDataDictionaryRemoteFieldService().buildRemotableFieldFromAttributeDefinition(field.getBusinessObjectClassName(), field.getPropertyName());
//                if (remotableAttributeField != null) {
//                    rowAttributeFields.add(remotableAttributeField);
//                }
//            }
//            attributeFields.addAll(rowAttributeFields);
//        }
//        return attributeFields;
//    }
/*
 * @@ -113,16 +113,19 @@
             Field chartField = FieldUtils.getPropertyField(alClass, "chartOfAccountsCode", true);
             chartField.setFieldDataType(SearchableAttribute.DATA_TYPE_STRING);
             displayedFieldNames.add("chartOfAccountsCode");
+            chartField.setColumnVisible(false);
             LookupUtils.setFieldQuickfinder(alBusinessObject, "chartOfAccountsCode", chartField, displayedFieldNames);

             Field orgField = FieldUtils.getPropertyField(orgClass, "organizationCode", true);
             orgField.setFieldDataType(SearchableAttribute.DATA_TYPE_STRING);
             displayedFieldNames.clear();
+            chartField.setColumnVisible(false);
             displayedFieldNames.add("organizationCode");
             LookupUtils.setFieldQuickfinder(new Account(), "organizationCode", orgField, displayedFieldNames);

             Field accountField = FieldUtils.getPropertyField(alClass, "accountNumber", true);
             accountField.setFieldDataType(SearchableAttribute.DATA_TYPE_STRING);
+            accountField.setColumnVisible(false);
             displayedFieldNames.clear();
             displayedFieldNames.add("accountNumber");
             LookupUtils.setFieldQuickfinder(alBusinessObject, "accountNumber", accountField, displayedFieldNames);

 */
}
