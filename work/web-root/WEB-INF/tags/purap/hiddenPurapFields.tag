<%--
 Copyright 2006-2007 The Kuali Foundation.
 
 Licensed under the Educational Community License, Version 1.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl1.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ taglib prefix="c" uri="/tlds/c.tld"%>
<%@ taglib prefix="fn" uri="/tlds/fn.tld"%>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html"%>
<%@ taglib uri="/tlds/struts-logic.tld" prefix="logic"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="kul"%>
<%@ taglib tagdir="/WEB-INF/tags/dd" prefix="dd"%>

<%-- PURCHASING ACCOUNTS PAYABLE DOCUMENT FIELDS --%>
<html:hidden property="document.purapDocumentIdentifier" />
<html:hidden property="document.statusCode" />
<html:hidden property="document.vendorHeaderGeneratedIdentifier" />
<html:hidden property="document.vendorDetailAssignedIdentifier" />

<%-- PURCHASING DOCUMENT FIELDS --%>
<html:hidden property="document.requisitionSourceCode" />
<html:hidden property="document.billingPhoneNumber" />
