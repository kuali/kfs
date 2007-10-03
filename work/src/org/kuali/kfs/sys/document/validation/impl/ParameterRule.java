/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.rules;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.core.bo.Parameter;
import org.kuali.core.bo.ParameterDetailType;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.ParameterService;

public class ParameterRule extends org.kuali.core.rules.ParameterRule {

    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        boolean result = super.processCustomRouteDocumentBusinessRules(document);

        result &= checkComponent((Parameter) getNewBo());

        return result;
    }

    public boolean checkComponent(Parameter param) {
        String component = param.getParameterDetailTypeCode();
        String namespace = param.getParameterNamespaceCode();
        boolean result = false;

        List<ParameterDetailType> dataDictionaryAndSpringComponents = SpringContext.getBean(ParameterService.class).getNonDatabaseDetailTypes();
        for (ParameterDetailType pdt : dataDictionaryAndSpringComponents) {
            if (pdt.getParameterNamespaceCode().equals(namespace) && pdt.getParameterDetailTypeCode().equals(component)) {
                result = true;
                break;
            }
        }

        if (!result) {
            Map<String, String> primaryKeys = new HashMap<String, String>(2);
            primaryKeys.put("parameterNamespaceCode", namespace);
            primaryKeys.put("parameterDetailTypeCode", component);
            result = ObjectUtils.isNotNull(getBoService().findByPrimaryKey(ParameterDetailType.class, primaryKeys));
        }

        if (!result) {
            putFieldError("parameterDetailTypeCode", "error.document.parameter.detailType.invalid", component);
        }

        return result;
    }

}
