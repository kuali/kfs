/*
 * Copyright 2014 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.document;

import java.util.Map;

import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Overridden to add informative help message
 */
public class ContractsAndGrantsCategoryMaintainableImpl extends FinancialSystemMaintainable {

    @Override
    public void processAfterPost(MaintenanceDocument document, Map<String, String[]> parameters) {
        super.processAfterPost(document, parameters);
        GlobalVariables.getMessageMap().putInfo(ArConstants.ContractsAndGrantsCategorySections.EDIT_CATEGORY, ArKeyConstants.ContractsGrantsCategoryConstants.CATEGORY_INFO);
    }

    @Override
    public void processAfterCopy(MaintenanceDocument document, Map<String, String[]> parameters) {
        super.processAfterCopy(document, parameters);
        GlobalVariables.getMessageMap().putInfo(ArConstants.ContractsAndGrantsCategorySections.EDIT_CATEGORY, ArKeyConstants.ContractsGrantsCategoryConstants.CATEGORY_INFO);
    }

    @Override
    public void processAfterEdit(MaintenanceDocument document, Map<String, String[]> requestParameters) {
        super.processAfterEdit(document, requestParameters);
        GlobalVariables.getMessageMap().putInfo(ArConstants.ContractsAndGrantsCategorySections.EDIT_CATEGORY, ArKeyConstants.ContractsGrantsCategoryConstants.CATEGORY_INFO);
    }

    @Override
    public void processAfterNew(MaintenanceDocument document, Map<String, String[]> requestParameters) {
        super.processAfterNew(document, requestParameters);
        GlobalVariables.getMessageMap().putInfo(ArConstants.ContractsAndGrantsCategorySections.EDIT_CATEGORY, ArKeyConstants.ContractsGrantsCategoryConstants.CATEGORY_INFO);
    }

    @Override
    public void processAfterAddLine(String colName, Class colClass) {
        super.processAfterAddLine(colName, colClass);
        GlobalVariables.getMessageMap().putInfo(ArConstants.ContractsAndGrantsCategorySections.EDIT_CATEGORY, ArKeyConstants.ContractsGrantsCategoryConstants.CATEGORY_INFO);
    }

}