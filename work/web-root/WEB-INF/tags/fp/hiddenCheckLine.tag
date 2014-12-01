<%--
   - The Kuali Financial System, a comprehensive financial management system for higher education.
   - 
   - Copyright 2005-2014 The Kuali Foundation
   - 
   - This program is free software: you can redistribute it and/or modify
   - it under the terms of the GNU Affero General Public License as
   - published by the Free Software Foundation, either version 3 of the
   - License, or (at your option) any later version.
   - 
   - This program is distributed in the hope that it will be useful,
   - but WITHOUT ANY WARRANTY; without even the implied warranty of
   - MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   - GNU Affero General Public License for more details.
   - 
   - You should have received a copy of the GNU Affero General Public License
   - along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
