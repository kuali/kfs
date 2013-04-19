<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<html:hidden property="anchor" />
<c:set var="detailAttributes" value="${DataDictionary.ProjectCodeImportDetail.attributes}" />
<c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />

<kul:tab tabTitle="Import Project Code" defaultOpen="true" tabErrorKey="${KFSConstants.ProjectCodeImportConstants.IMPORT_DETAILS_TAB_ERRORS}">
	<div class="tab-container" align=center>

		<table cellpadding="0" cellspacing="0" class="datatable" summary="Items Section">
			<tr>
				<c:choose>
					<c:when test="${readOnly}">
						<td colspan="9" class="subhead"><span class="subhead-left">New</td>
					</c:when>
					<c:otherwise>
						<td colspan="7" class="subhead"><span class="subhead-left">New</td>
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
	                      		'<img src="${ConfigProperties.externalizable.images.url}tinybutton-importlines.gif" title="import project codes from file" alt="import project codes from file"' +
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
				<kul:htmlAttributeHeaderCell attributeEntry="${detailAttributes.projectCode}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${detailAttributes.projectName}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${detailAttributes.projectManagerPrincipalName}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${detailAttributes.projectManagerUniversalId}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${detailAttributes.chartOfAccountsCode}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${detailAttributes.organizationCode}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${detailAttributes.projectDescription}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${detailAttributes.active}" />
			</tr>


			<logic:iterate indexId="ctr" name="KualiForm" property="document.projectCodeImportDetails" id="importLine">
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
					<td class="infoline"><kul:htmlControlAttribute attributeEntry="${detailAttributes.sequenceNumber}" property="document.projectCodeImportDetail[${ctr}].sequenceNumber" readOnly="true" /></td>
					<td class="infoline"><kul:htmlControlAttribute attributeEntry="${detailAttributes.projectCode}" property="document.projectCodeImportDetail[${ctr}].projectCode" readOnly="true" /></td>
					<td class="infoline"><kul:htmlControlAttribute attributeEntry="${detailAttributes.projectName}" property="document.projectCodeImportDetail[${ctr}].projectName" readOnly="true" /></td>
					<td class="infoline"><kul:htmlControlAttribute attributeEntry="${detailAttributes.projectManagerPrincipalName}"
							property="document.projectCodeImportDetail[${ctr}].projectManagerPrincipalName" readOnly="true" /></td>
					<td class="infoline"><kul:htmlControlAttribute attributeEntry="${detailAttributes.projectManagerUniversalId}" property="document.projectCodeImportDetail[${ctr}].projectManagerUniversal.name"
							readOnly="true" /></td>
					<td class="infoline"><kul:htmlControlAttribute attributeEntry="${detailAttributes.chartOfAccountsCode}" property="document.projectCodeImportDetail[${ctr}].chartOfAccountsCode"
							readOnly="true" /></td>
					<td class="infoline"><kul:htmlControlAttribute attributeEntry="${detailAttributes.organizationCode}" property="document.projectCodeImportDetail[${ctr}].organizationCode" readOnly="true" /></td>
					<td class="infoline"><kul:htmlControlAttribute attributeEntry="${detailAttributes.projectDescription}" property="document.projectCodeImportDetail[${ctr}].projectDescription" readOnly="true" /></td>
					<td class="infoline"><kul:htmlControlAttribute attributeEntry="${detailAttributes.active}" property="document.projectCodeImportDetail[${ctr}].active" readOnly="true" /></td>
				</tr>

			</logic:iterate>

		</table>

	</div>
</kul:tab>
