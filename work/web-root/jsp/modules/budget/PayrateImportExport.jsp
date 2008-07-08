<%--
 Copyright 2005-2007 The Kuali Foundation.
 
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
<%@ include file="/jsp/kfs/kfsTldHeader.jsp"%>

<c:set var="payrateImportExportAttributes"	value="${DataDictionary.PayrateImportExport.attributes}" />

<html:xhtml/>

<kul:page showDocumentInfo="false"
	htmlFormAction="budgetPayrateImportExport" renderMultipart="true"
	docTitle="Payrate Import/Export"
    transactionalDocument="false" showTabButtons="true">
    
    <html:hidden property="returnAnchor" />
    <html:hidden property="returnFormKey" />
    <html:hidden property="backLocation" />
    <html:hidden name="KualiForm" property="universityFiscalYear" />
	<kul:tabTop tabTitle="Payrate Export" defaultOpen="true">
		<div class="tab-container" align=center>
			<h3>Export</h3>
			<table bgcolor="#C0C0C0" cellpadding="30" >
				<tr>
					<td> 
						<b><kul:htmlAttributeLabel attributeEntry="${payrateImportExportAttributes.positionUnionCode}" /></b>
						<kul:htmlControlAttribute property="positionUnionCode" readOnly="false" attributeEntry="${payrateImportExportAttributes.positionUnionCode}"/>
					</td> 
					<td> 
						<b><kul:htmlAttributeLabel attributeEntry="${payrateImportExportAttributes.csfFreezeDate}" /></b>
						<kul:htmlControlAttribute property="csfFreezeDate" readOnly="false" attributeEntry="${payrateImportExportAttributes.csfFreezeDate}"/>
					</td>
					<td> 
						<b><kul:htmlAttributeLabel attributeEntry="${payrateImportExportAttributes.exportCount}" /></b>
						<kul:htmlControlAttribute property="exportCount" readOnly="true" attributeEntry="${payrateImportExportAttributes.exportCount}"/>
					</td>
				</tr>
				<tr>
					<td colspan="3">
						<div align="center">
							<html:image property="methodToCall.performExport" src="kr/static/images/buttonsmall_submit.gif"  title="Export" alt="Export" styleClass="tinybutton" /> &nbsp;&nbsp;&nbsp;
						
							<html:image property="methodToCall.close" src="kr/static/images/buttonsmall_close.gif"  title="Import" alt="Import" styleClass="tinybutton" /> &nbsp;&nbsp;&nbsp;
						</div>
					</td>
				</tr>
			</table>
		</div>
	</kul:tabTop>
	
	<kul:tab tabTitle="Payrate Import" defaultOpen="true">
		<div class="tab-container" align=center>
			<h3>Import</h3>
			<table bgcolor="#C0C0C0" >
				<tr>
					<td> 
						<b><kul:htmlAttributeLabel attributeEntry="${payrateImportExportAttributes.fileName}" /></b>
						<html:file property="file" />
					</td> 
					<td> 
						<b><kul:htmlAttributeLabel attributeEntry="${payrateImportExportAttributes.importCount}" /></b>
						<kul:htmlControlAttribute property="importCount" readOnly="true" attributeEntry="${payrateImportExportAttributes.importCount}"/>
					</td>
					<td> 
						<b><kul:htmlAttributeLabel attributeEntry="${payrateImportExportAttributes.updateCount}" /></b>
						<kul:htmlControlAttribute property="updateCount" readOnly="true" attributeEntry="${payrateImportExportAttributes.updateCount}"/>
					</td>
					
				</tr>
				<tr>
					<td colspan="3">
						<div align="center">
							<html:image property="methodToCall.performImport" src="kr/static/images/buttonsmall_submit.gif"  title="Import" alt="Import" styleClass="tinybutton" /> &nbsp;&nbsp;&nbsp;
						
							<html:image property="methodToCall.close" src="kr/static/images/buttonsmall_close.gif"  title="Import" alt="Import" styleClass="tinybutton" /> &nbsp;&nbsp;&nbsp;
						</div>
					</td>
				</tr>
			</table>
		</div>
	</kul:tab>
	
</kul:page>
