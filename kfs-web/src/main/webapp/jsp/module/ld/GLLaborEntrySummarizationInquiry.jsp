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

<c:set var="laborLedgerEntryAttributes" value="${DataDictionary.LedgerEntry.attributes}" />

<kul:page lookup="false" showDocumentInfo="false"
	htmlFormAction="laborGLLaborEntrySummarizationInquiry"
	headerTitle="${KualiForm.pageTitle}" docTitle="" transactionalDocument="false">
	<table width="100%">
		<tr>
			<td width="1%"><img src="${ConfigProperties.kr.externalizable.images.url}pixel_clear.gif" alt="" width="20" height="20" /></td>
			<td>
				<table width="100%" class="datatable-100">
					<tr>
						<th class="grid"><kul:htmlAttributeLabel attributeEntry="${laborLedgerEntryAttributes.universityFiscalYear}" noColon="true" useShortLabel="true"/></th>
						<th class="grid"><kul:htmlAttributeLabel attributeEntry="${laborLedgerEntryAttributes.universityFiscalPeriodCode}" noColon="true"/></th>
						<th class="grid"><kul:htmlAttributeLabel attributeEntry="${laborLedgerEntryAttributes.chartOfAccountsCode}" noColon="true"/></th>
						<th class="grid"><kul:htmlAttributeLabel attributeEntry="${laborLedgerEntryAttributes.accountNumber}" noColon="true"/></th>
						<th class="grid"><kul:htmlAttributeLabel attributeEntry="${laborLedgerEntryAttributes.subAccountNumber}" noColon="true"/></th>
						<th class="grid"><kul:htmlAttributeLabel attributeEntry="${laborLedgerEntryAttributes.financialObjectCode}" noColon="true"/></th>
						<th class="grid"><kul:htmlAttributeLabel attributeEntry="${laborLedgerEntryAttributes.financialSubObjectCode}" noColon="true"/></th>
					</tr>
					<tr>
						<td class="grid"><c:if test="${!empty KualiForm.universityFiscalYearInquiryUrl}"><a href="${KualiForm.universityFiscalYearInquiryUrl}" target="_blank"></c:if><kul:htmlControlAttribute attributeEntry="${laborLedgerEntryAttributes.universityFiscalYear}" property="universityFiscalYear" readOnly="true"/><c:if test="${!empty KualiForm.universityFiscalYearInquiryUrl}"></a></c:if></td>
						<td class="grid"><c:if test="${!empty KualiForm.universityFiscalPeriodCodeInquiryUrl}"><a href="${KualiForm.universityFiscalPeriodCodeInquiryUrl}" target="_blank"></c:if><kul:htmlControlAttribute attributeEntry="${laborLedgerEntryAttributes.universityFiscalPeriodCode}" property="universityFiscalPeriodCode" readOnly="true"/><c:if test="${!empty KualiForm.universityFiscalPeriodCodeInquiryUrl}"></a></c:if></td>
						<td class="grid"><c:if test="${!empty KualiForm.chartOfAccountsCodeInquiryUrl}"><a href="${KualiForm.chartOfAccountsCodeInquiryUrl}" target="_blank"></c:if><kul:htmlControlAttribute attributeEntry="${laborLedgerEntryAttributes.chartOfAccountsCode}" property="chartOfAccountsCode" readOnly="true"/><c:if test="${!empty KualiForm.chartOfAccountsCodeInquiryUrl}"></a></c:if></td>
						<td class="grid"><c:if test="${!empty KualiForm.accountNumberInquiryUrl}"><a href="${KualiForm.accountNumberInquiryUrl}" target="_blank"></c:if><kul:htmlControlAttribute attributeEntry="${laborLedgerEntryAttributes.accountNumber}" property="accountNumber" readOnly="true"/><c:if test="${!empty KualiForm.accountNumberInquiryUrl}"></a></c:if></td>
						<td class="grid"><c:if test="${!empty KualiForm.subAccountNumberInquiryUrl}"><a href="${KualiForm.subAccountNumberInquiryUrl}" target="_blank"></c:if><kul:htmlControlAttribute attributeEntry="${laborLedgerEntryAttributes.subAccountNumber}" property="subAccountNumber" readOnly="true"/><c:if test="${!empty KualiForm.subAccountNumberInquiryUrl}"></a></c:if></td>
						<td class="grid"><c:if test="${!empty KualiForm.financialObjectCodeInquiryUrl}"><a href="${KualiForm.financialObjectCodeInquiryUrl}" target="_blank"></c:if><kul:htmlControlAttribute attributeEntry="${laborLedgerEntryAttributes.financialObjectCode}" property="financialObjectCode" readOnly="true"/><c:if test="${!empty KualiForm.financialObjectCodeInquiryUrl}"></a></c:if></td>
						<td class="grid"><c:if test="${!empty KualiForm.financialSubObjectCodeInquiryUrl}"><a href="${KualiForm.financialSubObjectCodeInquiryUrl}" target="_blank"></c:if><kul:htmlControlAttribute attributeEntry="${laborLedgerEntryAttributes.financialSubObjectCode}" property="financialSubObjectCode" readOnly="true"/><c:if test="${!empty KualiForm.financialSubObjectCodeInquiryUrl}"></a></c:if></td>
					</tr>
				</table>
			</td>
			<td width="1%"><img src="${ConfigProperties.kr.externalizable.images.url}pixel_clear.gif" alt="" height="20" width="20" /></td>
		</tr>
		<tr>
			<td width="1%"><img src="${ConfigProperties.kr.externalizable.images.url}pixel_clear.gif" alt="" width="20" height="20" /></td>
			<td> 
			<c:if test="${!empty KualiForm.entries}">
	        
	        <display:table class="datatable-100" cellspacing="0"
				cellpadding="0" name="${KualiForm.entries}" id="row"
				export="true" pagesize="100" defaultsort="2"
				requestURI="laborGLLaborEntrySummarizationInquiry.do?methodToCall=start&universityFiscalYear=${KualiForm.universityFiscalYear}&universityFiscalPeriodCode=${KualiForm.universityFiscalPeriodCode}&chartOfAccountsCode=${KualiForm.chartOfAccountsCode}&accountNumber=${KualiForm.accountNumber}&subAccountNumber=${KualiForm.subAccountNumber}&financialObjectCode=${KualiForm.financialObjectCode}&financialSubObjectCode=${KualiForm.financialSubObjectCode}&financialBalanceTypeCode=${KualiForm.financialBalanceTypeCode}&financialObjectTypeCode=${KualiForm.financialObjectTypeCode}&financialDocumentTypeCode=${KualiForm.financialDocumentTypeCode}&financialSystemOriginationCode=${KualiForm.financialSystemOriginationCode}&documentNumber=${KualiForm.documentNumber}">
				
				<c:set var="columnLength" value="${fn:length(row.columns)-15}" />
				<c:forEach items="${row.columns}" var="column" varStatus="status">
					<c:if test="${!empty column.columnAnchor.title}">
						<c:set var="title" value="${column.columnAnchor.title}" />
					</c:if>
					<c:if test="${empty column.columnAnchor.title}">
						<c:set var="title" value="${column.propertyValue}" />
					</c:if>
					
					<display:column class="${column.formatter.implementationClass == 'org.kuali.rice.kns.web.format.CurrencyFormatter' ? 'numbercell' : 'infocell'}" sortable="${column.sortable}" decorator="org.kuali.rice.kns.web.ui.FormatAwareDecorator" title="${column.columnTitle}" comparator="${column.comparator}">
						<c:choose>
							<c:when test="${!empty column.propertyURL}">
								<a href="<c:out value="${column.propertyURL}"/>" title="<c:out value="${column.columnAnchor.title}" />" target="_blank"><c:out value="${column.propertyValue}" /></a>	
							</c:when>		
							<c:otherwise>
								<c:out value="${column.propertyValue}" />
							</c:otherwise>	
						</c:choose>
					</display:column>
				</c:forEach>
				
			</display:table>
			</td>
			</c:if>
			<td width="1%"><img src="${ConfigProperties.kr.externalizable.images.url}pixel_clear.gif" alt="" height="20" width="20" /></td>
		</tr>
	</table>
	
	<kul:inquiryControls />

</kul:page>
