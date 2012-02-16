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
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.krad.workflow.attribute.DataDictionarySearchableAttribute;

public class FinancialSystemSearchableAttribute extends DataDictionarySearchableAttribute {

    @Override
    public List<RemotableAttributeField> getSearchFields(ExtensionDefinition extensionDefinition, String documentTypeName) {
        List<Row> searchRows = getSearchingRows(documentTypeName);
        List<RemotableAttributeField> attributeFields = new ArrayList<RemotableAttributeField>();
        for (Row row : searchRows) {
            List<RemotableAttributeField> rowAttributeFields = new ArrayList<RemotableAttributeField>();
            for (Field field : row.getFields()) {
                RemotableAttributeField remotableAttributeField = new DataDictionaryRemoteFieldBuilder().buildRemotableFieldFromAttributeDefinition(field.getBusinessObjectClassName(), field.getPropertyName());
                if (remotableAttributeField != null) {
                    rowAttributeFields.add(remotableAttributeField);
                }
            }
            attributeFields.addAll(rowAttributeFields);
        }
        return attributeFields;
    }

}
