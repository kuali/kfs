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
package org.kuali.kfs.module.bc.document.web.struts;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.module.bc.BCKeyConstants;
import org.kuali.kfs.module.bc.service.PayrateImportService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;

public class PayrateImportExportAction extends BudgetExpansionAction {
    
    public ActionForward performImport(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PayrateImportExportForm payrateImportExportForm = (PayrateImportExportForm) form;
        PayrateImportService payrateImportService = SpringContext.getBean(PayrateImportService.class);
        List<String> messageList = new ArrayList<String>();
        //TODO: check that budget construction updates are allowed
        
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yyyy ' ' HH:mm:ss", Locale.US);
        
        boolean isValid = validateFormData(payrateImportExportForm);
        
        if (!isValid) {
            //TODO: add path to constants
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
        
        Date startTime = new Date();
        messageList.add("Import run started " + dateFormatter.format(startTime));
        
        StringBuilder parsingErrors = payrateImportService.importFile(payrateImportExportForm.getFile().getInputStream());
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    public ActionForward performExport(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    public boolean validateFormData(PayrateImportExportForm form) {
        boolean isValid = true;
        PayrateImportExportForm importForm = (PayrateImportExportForm) form;
        ErrorMap errorMap = GlobalVariables.getErrorMap();
        
        if ( importForm.getFile() == null || importForm.getFile().getFileSize() == 0 ) {
            errorMap.putError(KFSConstants.GLOBAL_ERRORS, BCKeyConstants.ERROR_FILE_IS_REQUIRED);
            isValid = false;
        }
        if ( importForm.getFile() != null && importForm.getFile().getFileSize() == 0 ) {
            errorMap.putError(KFSConstants.GLOBAL_ERRORS, BCKeyConstants.ERROR_FILE_EMPTY);
            isValid = false;
        }
        if (importForm.getFile() != null && (StringUtils.isBlank(importForm.getFile().getFileName())) ) {
            errorMap.putError(KFSConstants.GLOBAL_ERRORS, BCKeyConstants.ERROR_FILENAME_REQUIRED);
            isValid = false;
        }
        
        
        return isValid;
    }
}
