/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.module.ar.document.validation;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.ContractsAndGrantsCategories;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.service.FinancialSystemDocumentTypeService;
import org.kuali.kfs.sys.document.validation.impl.KfsMaintenanceDocumentRuleBase;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Validates content of a <code>{@link AccountDelegate}</code> maintenance document upon triggering of a approve, save, or route
 * event.
 */
public class CategoryRule extends KfsMaintenanceDocumentRuleBase {

    protected static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CategoryRule.class);

    protected ContractsAndGrantsCategories oldCategories;
    protected ContractsAndGrantsCategories newCategories;


    @Override
    public boolean processSaveDocument(Document document) {
        GlobalVariables.getMessageMap().putInfo("EditCategory", ArKeyConstants.ContractsGrantsCategoryConstants.CATEGORY_INFO);
        return super.processSaveDocument(document);
    }

    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {

        LOG.debug("Entering processCustomSaveDocumentBusinessRules()");
        setupConvenienceObjects(document);

        // check simple rules
        boolean success = checkSimpleRules();

        return success;
    }

    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        LOG.debug("Entering processCustomRouteDocumentBusinessRules()");

        setupConvenienceObjects(document);

        // check simple rules
        boolean success = checkSimpleRules();

        return success;
    }

    @Override
    protected boolean processCustomApproveDocumentBusinessRules(MaintenanceDocument document) {

        boolean success = true;

        LOG.debug("Entering processCustomApproveDocumentBusinessRules()");
        setupConvenienceObjects(document);

        // check simple rules
        success &= checkSimpleRules();

        return success;
    }

    protected void setupConvenienceObjects(MaintenanceDocument document) {

        // setup oldAccount convenience objects, make sure all possible sub-objects are populated
        oldCategories = (ContractsAndGrantsCategories) super.getOldBo();

        // setup newAccount convenience objects, make sure all possible sub-objects are populated
        newCategories = (ContractsAndGrantsCategories) super.getNewBo();
    }

    protected boolean checkSimpleRules() {
        GlobalVariables.getMessageMap().putInfo("EditCategory", ArKeyConstants.ContractsGrantsCategoryConstants.CATEGORY_INFO);
        boolean success = true;

        boolean isCategoryObjectCode =(ObjectUtils.isNotNull(newCategories.getCategoryObjectCodes()) && !StringUtils.isEmpty(newCategories.getCategoryObjectCodes()));
        boolean isCategoryConsolidation = (ObjectUtils.isNotNull(newCategories.getCategoryConsolidations()) && !StringUtils.isEmpty(newCategories.getCategoryConsolidations()));
        boolean isCategoryLevels = (ObjectUtils.isNotNull(newCategories.getCategoryLevels()) && !StringUtils.isEmpty(newCategories.getCategoryLevels()));

        if(!(isCategoryObjectCode || isCategoryConsolidation || isCategoryLevels)){
            GlobalVariables.getMessageMap().putError("EditCategory",ArKeyConstants.ContractsGrantsCategoryConstants.ERROR_ANY_ONE_REQUIRED);
            success &= false;
        }

       return success;
    }
}
