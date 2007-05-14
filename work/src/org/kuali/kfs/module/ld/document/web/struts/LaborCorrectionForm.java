/*
 * Copyright 2006-2007 The Kuali Foundation.
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

package org.kuali.module.labor.web.struts.form;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.upload.FormFile;
import org.kuali.Constants;
import org.kuali.core.document.authorization.TransactionalDocumentActionFlags;
import org.kuali.core.web.struts.form.KualiTableRenderFormMetadata;
import org.kuali.core.web.ui.Column;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.gl.bo.OriginEntry;
import org.kuali.module.gl.document.CorrectionDocument;
import org.kuali.module.gl.web.struts.form.CorrectionForm;
import org.kuali.module.gl.web.struts.form.GroupHolder;
import org.kuali.module.labor.bo.LaborOriginEntry;

public class LaborCorrectionForm extends CorrectionForm {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborCorrectionForm.class);

    private List<LaborOriginEntry> allEntries;
    private List<LaborOriginEntry> displayEntries;
    
    private LaborOriginEntry entryForManualEdit;

    public LaborCorrectionForm() {
        super();
    }
    public void clearForm() {
        super.clearForm();
        
        allEntries = new ArrayList<LaborOriginEntry>();
        displayEntries = new ArrayList<LaborOriginEntry>();
    }
 }
