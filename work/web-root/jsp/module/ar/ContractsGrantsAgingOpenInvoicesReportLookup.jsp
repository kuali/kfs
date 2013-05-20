<%--
 Copyright 2006-2008 The Kuali Foundation
 
 Licensed under the Educational Community License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl2.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<kul:page lookup="true" showDocumentInfo="false"
	htmlFormAction="${KualiForm.htmlFormAction}"
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
						<table width="25%" cellspacing="0" cellpadding="0">
							
								<c:if test="${param.accountNumber != null}" >
									<tr><td>Account Number:</td><td><c:out value="${param.accountNumber}" /></td>
								</c:if>
								<c:if test="${param.billingChartCode != null}" >
									<tr><td>Chart Code:</td><td><c:out value="${param.billingChartCode}" /></td></tr>
								</c:if>
								<c:if test="${param.organizationCode != null}" >
									<tr><td>Organization Code:</td><td><c:out value="${param.organizationCode}" /></td>
								</c:if>
							 
							<tr><td>Report Run Date:</td><td><c:out value="${param.reportRunDate}" /></td>
							<tr><td>Report Age:</td><td><c:out value="${param.columnTitle}" /></td>
						</table> <br><br>
				  
	      			<display:table class="datatable-100"
	      			               cellspacing="0"
								   cellpadding="0"
								   name="${reqSearchResults}"
								   id="row"
								   export="true"
				                   pagesize="100"
				                   defaultsort="4"
				                   defaultorder="descending"
				                   requestURI="arContractsGrantsAgingOpenInvoicesReportLookup.do?methodToCall=viewResults&reqSearchResultsSize=${reqSearchResultsSize}&searchResultKey=${searchResultKey}">

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
