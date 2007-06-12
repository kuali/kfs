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

<kul:documentPage showDocumentInfo="true"
    documentTypeName="KualiSalaryExpenseTransferDocument"
    htmlFormAction="laborSalaryExpenseTransfer" renderMultipart="true"
    showTabButtons="true">

    <html:hidden property="financialBalanceTypeCode" />
    <kul:hiddenDocumentFields />
    <kul:documentOverview editingMode="${KualiForm.editingMode}" />
    <kul:tab tabTitle="Employee Lookup" defaultOpen="true"
        tabErrorKey="${Constants.EMPLOYEE_LOOKUP_ERRORS}">
        <div class="tab-container" align=center>
            <div class="h2-container"><b>Employee Lookup</b></div>
            <table cellpadding="0" cellspacing="0" class="datatable"
                summary="employee lookup">
    
              <tr>
                <kul:htmlAttributeHeaderCell
                    attributeEntry="${DataDictionary.UniversalUser.attributes.personPayrollIdentifier}"
                    horizontal="true"
                    forceRequired="true"
                    />
                <td>
                        <ld:employee userIdFieldName="emplid" 
                                  userNameFieldName="user.personName" 
                                  fieldConversions="personPayrollIdentifier:emplid"
                                  lookupParameters="emplid:personPayrollIdentifier,universityFiscalYear:universityFiscalYear"
                                  hasErrors="${hasErrors}"
                                  onblur="${onblur}"
                                  highlight="${addHighlighting}">
                            <jsp:attribute name="helpLink" trim="true">
                                <kul:help
                                    businessObjectClassName="${field.businessObjectClassName}"
                                    attributeName="${field.fieldHelpName}"
                                    altText="${field.fieldHelpSummary}" />      
                            </jsp:attribute>
                        </ld:employee>
 
                </td>
              </tr>
              <tr>
                <kul:htmlAttributeHeaderCell
                    horizontal="true"
                    forceRequired="false"
                    literalLabel="Last Queried Fiscal Year"
                    />
                <td>${KualiForm.universityFiscalYear}&nbsp;</td>
              </tr>
            </table>
                <p>
        </div>
    </kul:tab>

      <c:set var="copyMethod" value="" scope="request"/>
      <c:set var="actionInfixVar" value="" scope="request"/>
      <c:set var="accountingLineIndexVar" value="" scope="request"/>
	<fin:accountingLines editingMode="${KualiForm.editingMode}"
		editableAccounts="${KualiForm.editableAccounts}" inherit="false"
		optionalFields="positionNumber,payrollEndDateFiscalYear,payrollEndDateFiscalPeriodCode,payrollTotalHours">

      <jsp:attribute name="groupsOverride">
      <table width="100%" border="0" cellpadding="0" cellspacing="0" class="datatable">
        <fin:subheadingWithDetailToggleRow
            columnCount="${columnCount}"
             subheading="Accounting Lines"/>
        <ld:importedAccountingLineGroup
            isSource="true"
            columnCountUntilAmount="${columnCountUntilAmount}"
            columnCount="${columnCount}"
            optionalFields="${optionalFieldsMap}"
            extraRowFields="${extraSourceRowFieldsMap}"
            editingMode="${KualiForm.editingMode}"
            editableAccounts="${editableAccountsMap}"
            editableFields="${KualiForm.accountingLineEditableFields}"
            debitCreditAmount="${debitCreditAmountString}"
            currentBaseAmount="${currentBaseAmountString}"
            extraHiddenFields="${extraHiddenFieldsMap}"
            useCurrencyFormattedTotal="${useCurrencyFormattedTotalBoolean}"
            includeObjectTypeCode="${includeObjectTypeCodeBoolean}"
            displayMonthlyAmounts="${displayMonthlyAmountsBoolean}"
            forcedReadOnlyFields="${KualiForm.forcedReadOnlyTargetFields}"
            accountingLineAttributes="${accountingLineAttributesMap}">
            <jsp:attribute name="importRowOverride">
                <html:image property="methodToCall.copyAllAccountingLines" src="${ConfigProperties.externalizable.images.url}tinybutton-copyall.gif" title="Copy all Source Accounting Lines" alt="Copy all Source Lines" styleClass="tinybutton"/>
   			        <html:image property="methodToCall.deleteAllAccountingLines"
					    src="${ConfigProperties.externalizable.images.url}tinybutton-deleteall.gif"
						title="Delete all Source Accounting Lines"
						alt="Delete all Source Lines" styleClass="tinybutton" />
                Import from Labor Ledger
                <gl:balanceInquiryLookup
                    boClassName="org.kuali.module.labor.bo.LedgerBalanceForSalaryExpenseTransfer"
                    actionPath="glBalanceInquiryLookup.do"
                    lookupParameters="emplid:emplid,financialBalanceTypeCode:financialBalanceTypeCode"
                    hideReturnLink="false" />
            </jsp:attribute>
            <jsp:attribute name="customActions">
                <c:set var="copyMethod" value="copyAccountingLine.line${accountingLineIndexVar}" scope="request" />
                <html:image property="methodToCall.${copyMethod}.anchoraccounting${actionInfixVar}Anchor" src="${ConfigProperties.externalizable.images.url}tinybutton-copy2.gif" title="Copy an Accounting Line" alt="Copy an Accounting Line" styleClass="tinybutton"/>
            </jsp:attribute>
        </ld:importedAccountingLineGroup>

        <ld:importedAccountingLineGroup
            isSource="false"
            columnCountUntilAmount="${columnCountUntilAmount}"
            columnCount="${columnCount}"
            optionalFields="${optionalFieldsMap}"
            extraRowFields="${extraTargetRowFieldsMap}"
            editingMode="${KualiForm.editingMode}"
            editableAccounts="${editableAccountsMap}"
            editableFields="${editableFieldsMap}"
            debitCreditAmount="${debitCreditAmountString}"
            currentBaseAmount="${currentBaseAmountString}"
            forcedReadOnlyFields="${KualiForm.forcedReadOnlyTargetFields}"
            extraHiddenFields="${extraHiddenFieldsMap}"
            useCurrencyFormattedTotal="${useCurrencyFormattedTotalBoolean}"
            includeObjectTypeCode="${includeObjectTypeCodeBoolean}"
            displayMonthlyAmounts="${displayMonthlyAmountsBoolean}"
            accountingLineAttributes="${accountingLineAttributesMap}">
            <jsp:attribute name="importRowOverride">
            </jsp:attribute>
         </ld:importedAccountingLineGroup>
      </table>
      </jsp:attribute>
    </fin:accountingLines>
    <ld:laborLedgerPendingEntries />
    <kul:notes />
    <kul:adHocRecipients />
    <kul:routeLog />
    <kul:panelFooter />
    <kul:documentControls transactionalDocument="true" />
</kul:documentPage>
