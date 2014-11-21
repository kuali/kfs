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

<c:set var="payrateImportExportAttributes"	value="${DataDictionary.PayrateImportExport.attributes}" />

<html:xhtml/>

<kul:page showDocumentInfo="false"
	htmlFormAction="budgetPayrateImportExport" renderMultipart="true"
	docTitle=""
    transactionalDocument="false" showTabButtons="true">
	
	<script type="text/javascript">
	
	        var excludeSubmitRestriction = true;
	
	</script>    
	    <br>
    <html:hidden property="returnAnchor" />
    <html:hidden property="returnFormKey" />
    <html:hidden property="backLocation" />
    <html:hidden name="KualiForm" property="universityFiscalYear" />
    
    <strong><h2>Payrate Import/Export </h2> </strong>
    <kul:tabTop tabTitle="Payrate Import/Export" defaultOpen="true">
		<div class="tab-container" align=center>
			<table >
			<tr>
					<td width="90%" colspan="2"><h3>Payrate Export</h3></td>
					<td width="10%" ><h3>Action</h3></td>
				</tr>
				<tr >
					<td class="infoline" > 
						<b>
								<kul:htmlAttributeLabel attributeEntry="${payrateImportExportAttributes.positionUnionCode}" /></b>
						<kul:lookup boClassName="org.kuali.kfs.module.bc.businessobject.BudgetConstructionPosition" 
								fieldConversions="positionUnionCode:positionUnionCode" 
								lookupParameters="positionUnionCode:positionUnionCode" /> 
								<kul:htmlControlAttribute property="positionUnionCode" readOnly="false" attributeEntry="${payrateImportExportAttributes.positionUnionCode}"/>
					</td> 
					<td class="infoline"  > 
						<b><kul:htmlAttributeLabel attributeEntry="${payrateImportExportAttributes.csfFreezeDate}" /></b>
						<kul:htmlControlAttribute property="csfFreezeDate" readOnly="false" attributeEntry="${payrateImportExportAttributes.csfFreezeDate}" datePicker="true" />
					</td>
					<td class="infoline"  >
						<div align="center">
							<html:image property="methodToCall.performExport" src="kr/static/images/buttonsmall_submit.gif"  title="Export" alt="Export" styleClass="tinybutton" /> &nbsp;&nbsp;&nbsp;
						</div>
					</td>
				</tr>
			</table>
		</div>
		<div class="tab-container" align=center>
			<table bgcolor="#C0C0C0" cellpadding="30" >
				<tr>
					<td width="90%"><h3>Payrate Import</h3></td>
					<td width="10%"><h3>Action</h3></td>
				</tr>
				
				<tr >
					<td class="infoline" > 
						<b><kul:htmlAttributeLabel attributeEntry="${payrateImportExportAttributes.fileName}" /></b>
						<html:file property="file" />
					</td> 
					<td class="infoline" colspan="2">
						<div align="center">
							<html:image property="methodToCall.performImport" src="kr/static/images/buttonsmall_submit.gif"  title="Import" alt="Import" styleClass="tinybutton" /> &nbsp;&nbsp;&nbsp;
						</div>
					</td>
				</tr>
				
			</table>
			<table bgcolor="#C0C0C0" cellpadding="0" cellspacing="0" >
				<tr align="center" >
					<td class="infoline" width="50%"></td>
					<td class="infoline"> 
						<html:image property="methodToCall.close" src="kr/static/images/buttonsmall_close.gif"  title="Import" alt="Import" styleClass="tinybutton" />
					</td>
				</tr>
			</table>
		</div>
		
	</kul:tabTop>
	<kul:panelFooter />
	
</kul:page>
