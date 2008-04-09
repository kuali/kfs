/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.module.cams.web.struts.form;

import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.PropertyUtils;
import org.kuali.core.authorization.AuthorizationConstants;
import org.kuali.core.service.BusinessObjectDictionaryService;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.web.struts.form.KualiTransactionalDocumentFormBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.cams.document.AssetTransferDocument;

public class AssetTransferForm extends KualiTransactionalDocumentFormBase {

    public AssetTransferForm() {
        super();
        setDocument(new AssetTransferDocument());
        // If this is not done, when document description error is there, message comes back with read-only mode
        Map<String, String> editModeMap = new HashMap<String, String>();
        editModeMap.put(AuthorizationConstants.TransactionalEditMode.FULL_ENTRY, "TRUE");
        setEditingMode(editModeMap);
    }

    public AssetTransferDocument getAssetTransferDocument() {
        return (AssetTransferDocument) getDocument();
    }

    @Override
    public void populate(HttpServletRequest request) {
        super.populate(request);
        DataDictionaryService dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);
        // TODO - When I use below method gets an error [error getting property value for accountingPeriod Property
        // 'accountingPeriod' has no getter method] while reloading a document
        // SpringContext.getBean(BusinessObjectDictionaryService.class).performForceUppercase(getAssetTransferDocument());
        // TODO So this is a hack to prevent that error
        performCustomForceUpperCase(dataDictionaryService);

    }

    private void performCustomForceUpperCase(DataDictionaryService dataDictionaryService) {
        AssetTransferDocument bo = getAssetTransferDocument();
        PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(AssetTransferDocument.class);
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            Class<?> propertyType = propertyDescriptor.getPropertyType();
            if (propertyType != null && propertyType.isAssignableFrom(String.class)) {
                String propertyName = propertyDescriptor.getName();
                String currValue = (String) ObjectUtils.getPropertyValue(bo, propertyName);
                if (currValue != null && dataDictionaryService.isAttributeDefined(AssetTransferDocument.class, propertyName).booleanValue() && dataDictionaryService.getAttributeForceUppercase(AssetTransferDocument.class, propertyName).booleanValue()) {
                    try {
                        PropertyUtils.setProperty(bo, propertyName, currValue.toUpperCase());
                    }
                    catch (Exception e) {
                        throw new RuntimeException("Error while performing uppercase on field " + propertyName, e);
                    }

                }
            }
        }
    }

}
