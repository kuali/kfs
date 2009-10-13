<%--
 Copyright 2007-2009 The Kuali Foundation
 
 Licensed under the Educational Community License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl2.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<%@ attribute name="propertyName" required="true" description="name of form property containing the Check" %>
<%@ attribute name="baselinePropertyName" required="false" description="name of form property containing the baselineCheck" %>
<%@ attribute name="displayHidden" required="true" %>


<html:hidden property="${propertyName}.documentNumber" write="${displayHidden}" />
<html:hidden property="${propertyName}.sequenceId" write="${displayHidden}" />
<html:hidden property="${propertyName}.financialDocumentDepositLineNumber" write="${displayHidden}" />
<html:hidden property="${propertyName}.versionNumber" write="${displayHidden}" />

<html:hidden property="${propertyName}.checkNumber" write="${displayHidden}" />
<html:hidden property="${propertyName}.checkDate" write="${displayHidden}" />
<html:hidden property="${propertyName}.description" write="${displayHidden}" />
<html:hidden property="${propertyName}.amount" write="${displayHidden}" />

<c:if test="${!empty baselinePropertyName}">
  <html:hidden property="${baselinePropertyName}.documentNumber" write="${displayHidden}" />
  <html:hidden property="${baselinePropertyName}.sequenceId" write="${displayHidden}" />
  <html:hidden property="${baselinePropertyName}.financialDocumentDepositLineNumber" write="${displayHidden}" />
  <html:hidden property="${baselinePropertyName}.versionNumber" write="${displayHidden}" />

  <html:hidden property="${baselinePropertyName}.checkNumber" write="${displayHidden}" />
  <html:hidden property="${baselinePropertyName}.checkDate" write="${displayHidden}" />
  <html:hidden property="${baselinePropertyName}.description" write="${displayHidden}" />
  <html:hidden property="${baselinePropertyName}.amount" write="${displayHidden}" />
</c:if>
<c:if test="${displayHidden}">
    <br>
</c:if>
