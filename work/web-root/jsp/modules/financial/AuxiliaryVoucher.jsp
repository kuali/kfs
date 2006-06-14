<%@ include file="/jsp/core/tldHeader.jsp"%>

<kul:documentPage showDocumentInfo="true"
	htmlFormAction="financialAuxiliaryVoucher"
	documentTypeName="KualiAuxiliaryVoucherDocument" renderMultipart="true"
	showTabButtons="true">

	<html:hidden property="document.nextSourceLineNumber" />
	<html:hidden property="document.nextTargetLineNumber" />
	<kul:hiddenDocumentFields />

	<kul:documentOverview editingMode="${KualiForm.editingMode}" />
	<!-- AUXILIARY VOUCHER SPECIFIC FIELDS -->
	<kul:tab tabTitle="Auxiliary Voucher Details" defaultOpen="true"
		tabErrorKey="${Constants.EDIT_AUXILIARY_VOUCHER_ERRORS}">

		<div class="tab-container" align="center">
		<div class="h2-container">
		<h2>Auxiliary Voucher Details</h2>
		</div>
		<table cellpadding="0" class="datatable"
			summary="view/edit ad hoc recipients">
			<tbody>

				<tr>
					<th width="35%" class="bord-l-b">
					<div align="right">${ConfigProperties.label.document.auxiliaryVoucher.accountingPeriod}:</div>
					</th>
					<td class="datacell-nowrap"><select name="selectedAccountingPeriod">
						<c:forEach items="${KualiForm.accountingPeriods}"
							var="accountingPeriod">
							<c:set var="accountingPeriodCompositeValue"
								value="${accountingPeriod.universityFiscalPeriodCode}${accountingPeriod.universityFiscalYear}" />
							<c:choose>
								<c:when
									test="${KualiForm.selectedAccountingPeriod==accountingPeriodCompositeValue}">
									<option
										value='<c:out value="${accountingPeriodCompositeValue}"/>'
										selected="selected"><c:out
										value="${accountingPeriod.universityFiscalPeriodName}" /></option>
								</c:when>
								<c:otherwise>
									<option
										value='<c:out value="${accountingPeriodCompositeValue}" />'><c:out
										value="${accountingPeriod.universityFiscalPeriodName}" /></option>
								</c:otherwise>
							</c:choose>
						</c:forEach>
					</select></td>
				</tr>
				<tr>
					<th width="35%" class="bord-l-b">
					<div align="right">${ConfigProperties.label.document.auziliaryVoucher.voucherType}:</div>
					</th>
					<td class="datacell-nowrap"><html:radio value="AVAC"
						property="document.typeCode" /> Accrual <html:radio value="AVRC"
						property="document.typeCode" /> Recode <html:radio value="AVAD"
						property="document.typeCode" /> Adjustment</td>
				</tr>
				<tr>
					<th width="35%" class="bord-l-b">
					<div align="right">${ConfigProperties.label.document.journalVoucher.reversalDate}:
					</div>
					</th>
					<td class="datacell-nowrap"><kul:dateInputNoAttributeEntry
						property="document.reversalDate" maxLength="10" size="10" /></td>
				</tr>
			</tbody>
		</table>
		</div>

	</kul:tab>
	<fin:voucherAccountingLines isDebitCreditAmount="true"
		editingMode="${KualiForm.editingMode}"
		editableAccounts="${KualiForm.editableAccounts}" />
	<kul:generalLedgerPendingEntries />

	<kul:notes />

	<kul:adHocRecipients />

	<kul:routeLog />

	<kul:panelFooter />

	<kul:documentControls
		transactionalDocument="${documentEntry.transactionalDocument}" />

</kul:documentPage>
