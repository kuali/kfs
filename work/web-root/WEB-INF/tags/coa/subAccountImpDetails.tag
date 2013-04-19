<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<html:hidden property="anchor" />
<c:set var="detailAttributes" value="${DataDictionary.SubAccountImportDetail.attributes}" />
<c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />

<kul:tab tabTitle="Import Sub-Account Code" defaultOpen="true" tabErrorKey="${KFSConstants.SubAccountImportConstants.IMPORT_DETAILS_TAB_ERRORS}">
	<div class="tab-container" align=center>

		<table cellpadding="0" cellspacing="0" class="datatable" summary="Items Section">
			<tr>
				<c:choose>
					<c:when test="${readOnly}">
						<td colspan="10" class="subhead"><span class="subhead-left">New</td>
					</c:when>
					<c:otherwise>
						<td colspan="8" class="subhead"><span class="subhead-left">New</td>
						<td colspan="1" class="subhead" align="right" nowrap="nowrap" style="border-left: none;"><SCRIPT type="text/javascript">	                		function hideImport() {
	                      		document.getElementById("showLink").style.display="inline";
	                      		document.getElementById("uploadDiv").style.display="none";
	                  		}
	                  		function showImport() {
	                      		document.getElementById("showLink").style.display="none";
	                      		document.getElementById("uploadDiv").style.display="inline";
	                  		}
	                  		document.write(
	                    		'<a id="showLink" href="#" onclick="showImport();return false;">' +
	                      		'<img src="${ConfigProperties.externalizable.images.url}tinybutton-importlines.gif" title="import sub-accounts from file" alt="import sub-accounts from file"' +
	                      		'     width=72 height=15 border=0 align="right" class="det-button">' +
	                    		'<\/a>' +
	                    		'<div id="uploadDiv" style="display:none;" >' +
	                      		'<html:file size="30" property="importFile" />' +
	                      		'<html:image property="methodToCall.importLines" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif"
	                                    styleClass="tinybutton" alt="add imported items" title="add imported items" />' +
	                      		'<html:image property="methodToCall.cancel" src="${ConfigProperties.externalizable.images.url}tinybutton-cancelimport.gif"
	                                    styleClass="tinybutton" alt="cancel import" title="cancel import" onclick="hideImport();return false;" />' +
	                    		'<\/div>');
	                		</SCRIPT>
							<NOSCRIPT>
								Import lines
								<html:file size="30" property="importFile" style="font:10px;height:16px;" />
								<html:image property="methodToCall.importItems" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" alt="add imported items" title="add imported items" />
							</NOSCRIPT></td>
						<td colspan="1" class="subhead" nowrap="nowrap" style="border-left: none; text-align: center"><html:image property="methodToCall.removeAll"
								src="${ConfigProperties.externalizable.images.url}tinybutton-deleteall.gif" styleClass="tinybutton" alt="delete all imported lines" title="delete all imported lines" /></td>
					</c:otherwise>
				</c:choose>
			</tr>

			<tr>
				<kul:htmlAttributeHeaderCell attributeEntry="${detailAttributes.sequenceNumber}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${detailAttributes.chartOfAccountsCode}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${detailAttributes.accountNumber}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${detailAttributes.subAccountNumber}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${detailAttributes.subAccountName}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${detailAttributes.active}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${detailAttributes.subAccountTypeCode}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${detailAttributes.financialReportChartCode}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${detailAttributes.finReportOrganizationCode}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${detailAttributes.financialReportingCode}" />
			</tr>


			<logic:iterate indexId="ctr" name="KualiForm" property="document.subAccountImportDetails" id="importLine">
				<c:set var="currentTabIndex" value="${KualiForm.currentTabIndex}" scope="request" />
				<c:set var="topLevelTabIndex" value="${KualiForm.currentTabIndex}" scope="request" />

				<!--  hit form method to increment tab index -->
				<c:set var="currentTab" value="${kfunc:getTabState(KualiForm, tabKey)}" />

				<%-- default to closed --%>
				<c:choose>
					<c:when test="${empty currentTab}">
						<c:set var="isOpen" value="true" />
					</c:when>
					<c:when test="${!empty currentTab}">
						<c:set var="isOpen" value="${currentTab == 'OPEN'}" />
					</c:when>
				</c:choose>

				<tr>
					<td class="infoline"><kul:htmlControlAttribute attributeEntry="${detailAttributes.sequenceNumber}" property="document.subAccountImportDetail[${ctr}].sequenceNumber" readOnly="true" /></td>
					<td class="infoline"><kul:htmlControlAttribute attributeEntry="${detailAttributes.chartOfAccountsCode}" property="document.subAccountImportDetail[${ctr}].chartOfAccountsCode" readOnly="true" /></td>
					<td class="infoline"><kul:htmlControlAttribute attributeEntry="${detailAttributes.accountNumber}" property="document.subAccountImportDetail[${ctr}].accountNumber" readOnly="true" /></td>
					<td class="infoline"><kul:htmlControlAttribute attributeEntry="${detailAttributes.subAccountNumber}" property="document.subAccountImportDetail[${ctr}].subAccountNumber" readOnly="true" /></td>
					<td class="infoline"><kul:htmlControlAttribute attributeEntry="${detailAttributes.subAccountName}" property="document.subAccountImportDetail[${ctr}].subAccountName" readOnly="true" /></td>
					<td class="infoline"><kul:htmlControlAttribute attributeEntry="${detailAttributes.active}" property="document.subAccountImportDetail[${ctr}].active" readOnly="true" /></td>
					<td class="infoline"><kul:htmlControlAttribute attributeEntry="${detailAttributes.subAccountTypeCode}" property="document.subAccountImportDetail[${ctr}].subAccountTypeCode" readOnly="true" /></td>
					<td class="infoline"><kul:htmlControlAttribute attributeEntry="${detailAttributes.financialReportChartCode}" property="document.subAccountImportDetail[${ctr}].financialReportChartCode"
							readOnly="true" /></td>
					<td class="infoline"><kul:htmlControlAttribute attributeEntry="${detailAttributes.finReportOrganizationCode}" property="document.subAccountImportDetail[${ctr}].finReportOrganizationCode"
							readOnly="true" /></td>
					<td class="infoline"><kul:htmlControlAttribute attributeEntry="${detailAttributes.financialReportingCode}" property="document.subAccountImportDetail[${ctr}].financialReportingCode"
							readOnly="true" /></td>
				</tr>

			</logic:iterate>

		</table>

	</div>
</kul:tab>
