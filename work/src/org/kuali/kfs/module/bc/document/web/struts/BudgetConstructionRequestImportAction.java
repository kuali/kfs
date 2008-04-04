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
package org.kuali.module.budget.web.struts.action;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.web.struts.action.KualiAction;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.budget.BCConstants;
import org.kuali.module.budget.bo.BudgetConstructionRequestMove;
import org.kuali.module.budget.web.struts.form.BudgetConstructionRequestImportForm;


/**
 * Handles Budget Construction Import Requests
 */
public class BudgetConstructionRequestImportAction extends KualiAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetConstructionRequestImportAction.class);
    
    /**
     * Imports file
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward importFile(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BudgetConstructionRequestImportForm budgetConstructionImportForm = (BudgetConstructionRequestImportForm) form;
        FormFile fileToParse = budgetConstructionImportForm.getFile();
        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        
        //TODO: error checking?
        //TODO: text fields?
        //TODO: different parsing for different file types?
        /*BufferedReader fileReader = new BufferedReader(new InputStreamReader(fileToParse.getInputStream()));
        while (fileReader.ready()) {
            String[] objectAttributes = fileReader.readLine().split(budgetConstructionImportForm.getFieldDelimiter());
            BudgetConstructionRequestMove objectToSave = createBudgetConstructionRequestMoveObject(objectAttributes);
            if (objectToSave != null) businessObjectService.save(objectToSave);
        }*/
        
        String basePath = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSConstants.APPLICATION_URL_KEY);
        String lookupUrl = basePath + "/" + BCConstants.BC_SELECTION_ACTION + "?methodToCall=loadExpansionScreen";
        
        return new ActionForward(lookupUrl, true);
      
    }
    
    /**
     * returns to budget construction selection page without importing the file
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward cancelImport(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String basePath = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSConstants.APPLICATION_URL_KEY);
        String lookupUrl = basePath + "/" + BCConstants.BC_SELECTION_ACTION + "?methodToCall=loadExpansionScreen";
        
        return new ActionForward(lookupUrl, true);

    }
    
    /**
     * Creates BudgetConstructionRequestMove objects from each line of the imported file
     * 
     * @param attributes
     * @return
     */
    private BudgetConstructionRequestMove createBudgetConstructionRequestMoveObject(String[] attributes, String textFieldDelimeter) {
        
        return null;
    }
}
