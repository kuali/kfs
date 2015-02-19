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

<kul:page showDocumentInfo="false"
	htmlFormAction="budgetReportRunner" renderMultipart="true"
	showTabButtons="false"
	docTitle="Document Reports and Exports"
    transactionalDocument="false"
	>
              <html:hidden property="returnAnchor" />
              <html:hidden property="returnFormKey" />
              <html-el:hidden name="KualiForm" property="backLocation" />
              <html:hidden property="documentNumber" />
              <html:hidden property="universityFiscalYear" />
              <html:hidden property="chartOfAccountsCode" />
              <html:hidden property="accountNumber" />
              <html:hidden property="subAccountNumber" />
    <html:hidden property="mainWindow" />
	
    <bc:budgetConstructionReportDumpLines />

	<kul:panelFooter />

    <div id="globalbuttons" class="globalbuttons">
<%--
        <html:image src="${ConfigProperties.externalizable.images.url}buttonsmall_genpdf.gif" styleClass="globalbuttons" property="methodToCall.performReport" title="Perform Report" alt="Perform Report" onclick="excludeSubmitRestriction=true"/>
--%>
        <html:image src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_close.gif" styleClass="globalbuttons" property="methodToCall.close" onclick="excludeSubmitRestriction=true" title="close" alt="close"/>
    </div>

</kul:page>
