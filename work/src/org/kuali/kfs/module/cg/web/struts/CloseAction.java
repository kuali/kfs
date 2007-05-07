/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package org.kuali.module.cg.web.struts.action;

import org.kuali.core.web.struts.action.KualiTransactionalDocumentActionBase;
import org.kuali.module.cg.bo.Close;
import org.kuali.module.cg.document.CloseDocument;
import org.kuali.module.cg.web.struts.form.CloseForm;
import org.kuali.module.cg.lookup.valuefinder.NextCloseNumberFinder;
import org.kuali.kfs.util.SpringServiceLocator;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * User: Laran Evans <lc278@cornell.edu>
 * Date: Apr 19, 2007
 * Time: 12:01:25 PM
 */
public class CloseAction extends KualiTransactionalDocumentActionBase {}
