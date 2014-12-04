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
<a name="${TEMConstants.SOURCE_ANCHOR }" id="${TEMConstants.SOURCE_ANCHOR }"></a>
<kul:tab tabTitle="Accounting Lines" defaultOpen="true" tabErrorKey="sourceAccountingLines,newSourceLine*">
	<c:if test="${fullEntryMode }">
		<div class="tab-container" align="left">
	    	<c:if test="${fn:length(KualiForm.document.sourceAccountingLines) > 0}" >
	    		<html:image property="methodToCall.resetAccountingLines" src="${ConfigProperties.externalizable.images.url}tinybutton-restartaccountinglines.gif" alt="Restart Accounting Lines" title="Restart Accounting Lines" styleClass="tinybutton" />    		
			</c:if>
		</div>
	</c:if>
	<sys-java:accountingLines>
		<sys-java:accountingLineGroup newLinePropertyName="newSourceLine" collectionPropertyName="document.sourceAccountingLines" collectionItemPropertyName="document.sourceAccountingLine" attributeGroupName="source" />
	</sys-java:accountingLines>
</kul:tab>
