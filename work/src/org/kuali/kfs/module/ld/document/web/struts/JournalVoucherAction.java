/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.module.labor.web.struts.action;

import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.Constants;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.UrlFactory;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.module.labor.LaborConstants;

/**
 * This class piggy backs on all of the functionality in the KualiTransactionalDocumentActionBase but is necessary for this document
 * type. The Journal Voucher is unique in that it defines several fields that aren't typically used by the other financial
 * transaction processing eDocs (i.e. external system fields, object type override, credit and debit amounts).
 */
public class JournalVoucherAction extends org.kuali.module.financial.web.struts.action.JournalVoucherAction {
    
    /**
     * @see org.kuali.core.web.struts.action.KualiAction#performLookup(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward performLookup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String path = super.performLookup(mapping, form, request, response).getPath();
        path = path.replaceFirst(Constants.LOOKUP_ACTION, LaborConstants.LONG_ROW_TABLE_INRUIRY_ACTION);
        return new ActionForward(path, true);
    }
}
