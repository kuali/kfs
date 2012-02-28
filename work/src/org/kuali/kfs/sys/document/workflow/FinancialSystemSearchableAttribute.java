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
import java.util.Collection;
import java.util.List;

import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.uif.RemotableAbstractWidget;
import org.kuali.rice.core.api.uif.RemotableAttributeField;
import org.kuali.rice.core.api.uif.RemotableQuickFinder;
import org.kuali.rice.kew.api.extension.ExtensionDefinition;
import org.kuali.rice.kns.lookup.LookupUtils;
import org.kuali.rice.krad.workflow.attribute.DataDictionarySearchableAttribute;

//RICE20 This class needs to be fixed to support pre-rice2.0 features
public class FinancialSystemSearchableAttribute extends DataDictionarySearchableAttribute {

    @Override
    public List<RemotableAttributeField> getSearchFields(ExtensionDefinition extensionDefinition, String documentTypeName) {
        // TODO Auto-generated method stub
        List<RemotableAttributeField> orignalFields = super.getSearchFields(extensionDefinition, documentTypeName);
        List<RemotableAttributeField> fields = new ArrayList<RemotableAttributeField>( orignalFields.size() );
        for ( RemotableAttributeField field : orignalFields ) {
            if ( field.getName().equals(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE) ) {
                RemotableAttributeField.Builder newField = RemotableAttributeField.Builder.create(field);
                RemotableQuickFinder.Builder qf = RemotableQuickFinder.Builder.create(LookupUtils.getBaseLookupUrl(false), Chart.class.getName() );
                Collection<RemotableAbstractWidget.Builder> widgets = new ArrayList<RemotableAbstractWidget.Builder>(1);
                widgets.add(qf);
                newField.setWidgets( widgets );
                fields.add( newField.build() );
            } else {
                fields.add(field);
            }

            // RICE20: other fields
            // RICE20: add field conversions to widget

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
