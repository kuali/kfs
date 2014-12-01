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

<c:set var="balanceInquiryAttributes"
	value="${DataDictionary.LedgerBalanceForBenefitExpenseTransfer.attributes}" />
	
<c:set var="readOnly"
	value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT] || !KualiForm.editingMode['ledgerBalanceImporting']}"/>

<c:set var="documentTypeName" value="SalaryExpenseTransferDocument"/>
<c:set var="htmlFormAction" value="laborSalaryExpenseTransfer"/>

<c:if test="${isYearEnd}">
  <c:set var="documentTypeName" value="YearEndSalaryExpenseTransferDocument"/>
  <c:set var="htmlFormAction" value="laborYearEndSalaryExpenseTransfer"/>
</c:if>

<kul:documentPage showDocumentInfo="true"
    documentTypeName="${documentTypeName}"
    htmlFormAction="${htmlFormAction}" renderMultipart="true"
    showTabButtons="true">

    <html:hidden property="financialBalanceTypeCode" />
    
    <sys:documentOverview editingMode="${KualiForm.editingMode}" />
 
	<kul:tab tabTitle="Ledger Balance Importing" defaultOpen="true"
		tabErrorKey="${KFSConstants.EMPLOYEE_LOOKUP_ERRORS}">
		<div class="tab-container" align=center>
		<h3>Ledger Balance Importing</h3>
		<table cellpadding="0" cellspacing="0" class="datatable"
			summary="Ledger Balance Importing">

			<tr>
				<kul:htmlAttributeHeaderCell
					attributeEntry="${balanceInquiryAttributes.universityFiscalYear}"
					horizontal="true" width="35%"  labelFor="universityFiscalYear" forceRequired="true"/>

				<td class="datacell-nowrap"><kul:htmlControlAttribute
					attributeEntry="${balanceInquiryAttributes.universityFiscalYear}"
					property="universityFiscalYear" forceRequired="true" readOnly="${readOnly}" /> 
					
					<c:if test="${!readOnly}">
					<!-- KULLAB-709 Force field conversions -->
						<kul:lookup	boClassName="org.kuali.kfs.sys.businessobject.SystemOptions"
						lookupParameters="universityFiscalYear:universityFiscalYear"
						fieldConversions="universityFiscalYear:universityFiscalYear"						
						fieldLabel="${balanceInquiryAttributes.universityFiscalYear.label}" />
					</c:if>
				</td>
			</tr>			

             <tr>
               <kul:htmlAttributeHeaderCell
                   attributeEntry="${DataDictionary.PersonImpl.attributes.employeeId}"
                   horizontal="true"
                   forceRequired="true" labelFor="document.emplid"
                   />
               <td>
                     <sys:employee userIdFieldName="document.emplid"
                                 userNameFieldName="document.user.name" 
                                 fieldConversions="employeeId:document.emplid"
                                 lookupParameters="document.emplid:employeeId,universityFiscalYear:universityFiscalYear"
                                 hasErrors="${hasErrors}"
                                 onblur="${onblur}"
                                 highlight="${addHighlighting}" readOnly="${readOnly}" >
                     </sys:employee>
               </td>
             </tr>
            
            <tr>
            	<td height="30" class="infoline">&nbsp;</td>
            	<td height="30" class="infoline">
            		<c:if test="${!readOnly}">
	                   <gl:balanceInquiryLookup
	                       boClassName="org.kuali.kfs.module.ld.businessobject.LedgerBalanceForSalaryExpenseTransfer"
	                       actionPath="glBalanceInquiryLookup.do"
	                       lookupParameters="universityFiscalYear:universityFiscalYear,document.emplid:emplid,financialBalanceTypeCode:financialBalanceTypeCode"
	                       tabindexOverride="KualiForm.currentTabIndex"
	                       hideReturnLink="false" image="buttonsmall_search.gif"/>
	                </c:if>
				</td>
			</tr>
		</table>
		</div>
	</kul:tab>
	
	<kul:tab tabTitle="Accounting Lines" defaultOpen="true"	tabErrorKey="${KFSConstants.ACCOUNTING_LINE_ERRORS}">			
		<sys-java:accountingLines>
			<sys-java:accountingLineGroup collectionPropertyName="document.sourceAccountingLines" collectionItemPropertyName="document.sourceAccountingLine"	attributeGroupName="source" />
	       	<sys-java:accountingLineGroup collectionPropertyName="document.targetAccountingLines" collectionItemPropertyName="document.targetAccountingLine" attributeGroupName="target" />
		</sys-java:accountingLines>
	</kul:tab>
	
	<ld:errorCertification />
	     
	<ld:laborLedgerPendingEntries />
    <kul:notes />
    <kul:adHocRecipients />
    <kul:routeLog />
    <kul:superUserActions />
    <kul:panelFooter />
    <sys:documentControls transactionalDocument="true" extraButtons="${KualiForm.extraButtons}" />
</kul:documentPage>
