/*
 * Copyright 2005-2006 The Kuali Foundation
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
package org.kuali.kfs.fp.document.web.struts;

import org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase;


/**
 * This class extends the parent FinancialSystemTransactionalDocumentActionBase class, which contains all common action methods. Since the TOF
 * follows the basic transactional document pattern, there are no specific actions that it has to implement; however, this empty
 * class is necessary for integrating into the framework.
 */
public class TransferOfFundsAction extends KualiAccountingDocumentActionBase {
}
