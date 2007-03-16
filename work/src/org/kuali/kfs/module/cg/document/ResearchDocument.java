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
package org.kuali.module.kra.document;

import java.util.List;

import org.kuali.core.document.TransactionalDocument;
import org.kuali.module.kra.bo.AdhocOrg;
import org.kuali.module.kra.bo.AdhocPerson;
import org.kuali.module.kra.bo.AdhocWorkgroup;


/**
 * Research Administration Document Interface
 */
public interface ResearchDocument extends TransactionalDocument {
    public void initialize();
    public List<AdhocPerson> getAdhocPersons();
    public List<AdhocOrg> getAdhocOrgs();
    public List<AdhocWorkgroup> getAdhocWorkgroups();
    public String buildAdhocOrgReportXml(String permissionTypeCode, boolean encloseContent);
}