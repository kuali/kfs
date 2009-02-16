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

<%@ include file="/jsp/kfs/kfsTldHeader.jsp"%>

<c:set var="balanceInquiryAttributes"
	value="${DataDictionary.LedgerBalanceForBenefitExpenseTransfer.attributes}" />
	
<c:set var="readOnly"
	value="${empty KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />

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
    
    <kfs:documentOverview editingMode="${KualiForm.editingMode}" />
 
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
						<kul:lookup	boClassName="org.kuali.kfs.sys.businessobject.SystemOptions"
						lookupParameters="universityFiscalYear:universityFiscalYear"
						fieldLabel="${balanceInquiryAttributes.universityFiscalYear.label}" />
					</c:if>
				</td>
			</tr>			

             <tr>
               <kul:htmlAttributeHeaderCell
                   attributeEntry="${DataDictionary.PersonImpl.attributes.employeeId}"
                   horizontal="true"
                   forceRequired="true" labelFor="emplid"
                   />
               <td>
                     <kfs:employee userIdFieldName="emplid"
                                 userNameFieldName="user.name" 
                                 fieldConversions="employeeId:emplid"
                                 lookupParameters="emplid:employeeId,universityFiscalYear:universityFiscalYear"
                                 hasErrors="${hasErrors}"
                                 onblur="${onblur}"
                                 highlight="${addHighlighting}" readOnly="${disabled}" >
                     </kfs:employee>
               </td>
             </tr>
            
            <tr>
            	<td height="30" class="infoline">&nbsp;</td>
            	<td height="30" class="infoline">
            		<c:if test="${!readOnly}">
	                   <gl:balanceInquiryLookup
	                       boClassName="org.kuali.kfs.module.ld.businessobject.LedgerBalanceForSalaryExpenseTransfer"
	                       actionPath="glBalanceInquiryLookup.do"
	                       lookupParameters="universityFiscalYear:universityFiscalYear,emplid:emplid,financialBalanceTypeCode:financialBalanceTypeCode"
	                       tabindexOverride="KualiForm.currentTabIndex"
	                       hideReturnLink="false" image="buttonsmall_search.gif"/>
	                </c:if>
				</td>
			</tr>
		</table>
		</div>
	</kul:tab>
	
	<kul:tab tabTitle="Accounting Lines" defaultOpen="true"	tabErrorKey="${KFSConstants.ACCOUNTING_LINE_ERRORS}">			
		<sys:accountingLines>
			<sys:accountingLineGroup collectionPropertyName="document.sourceAccountingLines" collectionItemPropertyName="document.sourceAccountingLine"	attributeGroupName="source" />
	       	<sys:accountingLineGroup collectionPropertyName="document.targetAccountingLines" collectionItemPropertyName="document.targetAccountingLine" attributeGroupName="target" />
		</sys:accountingLines>
	</kul:tab> 
	     
	<ld:laborLedgerPendingEntries />
    <kul:notes />
    <kul:adHocRecipients />
    <kul:routeLog />
    <kul:panelFooter />
    <kfs:documentControls transactionalDocument="true" extraButtons="${KualiForm.extraButtons}" />
</kul:documentPage>
