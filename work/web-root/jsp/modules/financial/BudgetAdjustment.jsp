<%@ include file="/jsp/core/tldHeader.jsp" %>
<kul:documentPage showDocumentInfo="true" documentTypeName="KualiBudgetAdjustmentDocument" htmlFormAction="financialBudgetAdjustment"
                  renderMultipart="true" showTabButtons="true">

		<html:hidden property="document.nextSourceLineNumber"/>
		<html:hidden property="document.nextTargetLineNumber"/>

		<kul:hiddenDocumentFields />

        <kul:documentOverview editingMode="${KualiForm.editingMode}">
            <c:set var="baAttributes" value="${DataDictionary.KualiBudgetAdjustmentDocument.attributes}" />
            <tr><td>&nbsp;</td></tr>
            <tr>
              <kul:htmlAttributeHeaderCell
                  labelFor="document.postingYear"
                  attributeEntry="${baAttributes.postingYear}"
                  horizontal="true"
                  />
              <td align="left" valign="middle">
                <kul:htmlControlAttribute 
                    property="document.postingYear" 
                    attributeEntry="${baAttributes.postingYear}" 
                    readOnly="${readOnly}"/>
              </td>
              <th colspan="2">&nbsp;</th>
            </tr>
        </kul:documentOverview>

        <fin:accountingLines 
            editingMode="${KualiForm.editingMode}" 
            editableAccounts="${KualiForm.editableAccounts}" 
            extraHiddenFields="budgetAdjustmentPeriodCode"
            currentBaseAmount="true"
            displayMonthlyAmounts="true"
            />

		<kul:generalLedgerPendingEntries/>

		<kul:notes/>

		<kul:adHocRecipients/>

		<kul:routeLog/>

		<kul:panelFooter/>

		<kul:documentControls transactionalDocument="true" />

</kul:documentPage>