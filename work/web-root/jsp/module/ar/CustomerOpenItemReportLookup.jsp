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

<kul:page lookup="true" showDocumentInfo="false"
	htmlFormAction="arCustomerOpenItemReportLookup"
	headerMenuBar="${KualiForm.lookupable.htmlMenuBar}"
	headerTitle="Lookup" docTitle="" transactionalDocument="false">

	<div class="headerarea-small" id="headerarea-small">
	<h1><c:out value="${param.reportName}" />
	<kul:help resourceKey="lookupHelpText" altText="lookup help" /></h1>
	</div>
	
	<h3>
		<table width="100%" cellspacing="0" cellpadding="0">
			<tr>
				<td width="1%"><img src="${ConfigProperties.kr.externalizable.images.url}pixel_clear.gif" alt="" width="20" height="20" /></td>
				<td>Customer Number: &nbsp; <c:out value="${param.customerNumber}" />&nbsp;&nbsp;<c:out value="${param.customerName}" /></td>
			</tr>
		</table>
	</h3>

	<kul:enterKey methodToCall="search" />

	<html-el:hidden name="KualiForm" property="backLocation" />
	<html-el:hidden name="KualiForm" property="formKey" />
	<html-el:hidden name="KualiForm" property="lookupableImplServiceName" />
	<html-el:hidden name="KualiForm" property="businessObjectClassName" />
	<html-el:hidden name="KualiForm" property="conversionFields" />
	<html-el:hidden name="KualiForm" property="hideReturnLink" />

	<kul:errors errorTitle="Errors found in Search Criteria:" />

	<table width="100%" cellspacing="0" cellpadding="0">
		<tr>
			<td width="1%"><img src="${ConfigProperties.kr.externalizable.images.url}pixel_clear.gif" alt="" width="20" height="20" /></td>
			<td>
				<c:if test="${empty reqSearchResultsSize}">
					There were no results found.
				</c:if>
				<c:if test="${!empty reqSearchResultsSize}">
					<c:if test="${param.reportName == KFSConstants.CustomerOpenItemReport.OPEN_ITEM_REPORT_NAME}">
						<table width="25%" cellspacing="0" cellpadding="0">
							<tr><td>Report Option:</td><td><c:out value="${param.reportOption}" /></td>
							 <c:choose>
								<c:when test="${param.reportOption == KFSConstants.CustomerOpenItemReport.REPORT_OPTION_ACCT}" >
									<tr><td>Account Number:</td><td><c:out value="${param.accountNumber}" /></td>
								</c:when>
								<c:otherwise>
									<tr><td>Chart Code:</td><td><c:out value="${param.chartCode}" /></td>
									<tr><td>Organization Code:</td><td><c:out value="${param.orgCode}" /></td>
								</c:otherwise>
							 </c:choose>
							<tr><td>Report Run Date:</td><td><c:out value="${param.reportRunDate}" /></td>
							<tr><td>Report Age:</td><td><c:out value="${param.columnTitle}" /></td>
						</table> <br><br>
					</c:if>
				  
	      			<display:table class="datatable-100"
	      			               cellspacing="0"
								   cellpadding="0"
								   name="${reqSearchResults}"
								   id="row"
								   export="true"
				                   pagesize="100"
				                   defaultsort="4"
				                   defaultorder="descending"
				                   requestURI="arCustomerOpenItemReportLookup.do?methodToCall=viewResults&reqSearchResultsSize=${reqSearchResultsSize}&searchResultKey=${searchResultKey}">

					<c:forEach items="${row.columns}" var="column">
						<c:choose>
							<c:when test="${column.formatter.implementationClass == 'org.kuali.rice.core.web.format.CurrencyFormatter'}">
								<display:column class="numbercell"
												sortable="true"
												decorator="org.kuali.rice.kns.web.ui.FormatAwareDecorator"
												title="${column.columnTitle}"
												comparator="${column.comparator}">
									<c:choose>
										<c:when test="${column.propertyURL != \"\"}">
											<a href="<c:out value="${column.propertyURL}"/>"
											   title="${column.propertyValue}"
											   target="blank">
											   <c:out value="${column.propertyValue}" />
											</a>	
										</c:when>
										<c:otherwise>
											<c:out value="${column.propertyValue}" />
										</c:otherwise>
									</c:choose>
								</display:column>
							</c:when>
							<c:otherwise>
								<display:column class="infocell"
										        sortable="${column.sortable}"
										        decorator="org.kuali.rice.kns.web.ui.FormatAwareDecorator"
										        title="${column.columnTitle}"
										        comparator="${column.comparator}">
									<c:choose>
										<c:when test="${column.propertyURL != \"\"}">
											<a href="<c:out value="${column.propertyURL}"/>"
										   	   title="${column.propertyValue}"
										       target="blank">
										       <c:out value="${column.propertyValue}" />
										    </a>
										</c:when>
										<c:otherwise>
											<c:out value="${column.propertyValue}" />
										</c:otherwise>
									</c:choose>
								</display:column>
							</c:otherwise>
						</c:choose>	
					</c:forEach>
					</display:table>
			    </c:if>
			</td>
			<td width="1%"><img src="${ConfigProperties.kr.externalizable.images.url}pixel_clear.gif" alt="" height="20" width="20">
			</td>
		</tr>
	</table>
</kul:page>
