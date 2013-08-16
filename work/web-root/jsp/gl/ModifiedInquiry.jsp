<%--
 Copyright 2006 The Kuali Foundation
 
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
	htmlFormAction="glModifiedInquiry"
	headerMenuBar="${KualiForm.lookupable.htmlMenuBar}"
	headerTitle="Lookup" docTitle="" transactionalDocument="false">
	<div class="headerarea-small" id="headerarea-small">
	<h1><c:out value="${KualiForm.lookupable.title}" /> <kul:help
		resourceKey="lookupHelpText" altText="lookup help" /></h1>
	</div>
	<kul:enterKey methodToCall="search" />
	<html-el:hidden name="KualiForm" property="backLocation" />
	<html-el:hidden name="KualiForm" property="formKey" />
	<html-el:hidden name="KualiForm" property="lookupableImplServiceName" />
	<html-el:hidden name="KualiForm" property="businessObjectClassName" />
	<html-el:hidden name="KualiForm" property="conversionFields" />
	<html-el:hidden name="KualiForm" property="hideReturnLink" />
	<kul:errors errorTitle="Errors found in Search Criteria:" />
	<table width="1000px" cellspacing="0" cellpadding="0">
		<tr>
			<td width="1%"><img
				src="${ConfigProperties.kr.externalizable.images.url}pixel_clear.gif"
				alt="" width="20" height="20" /></td>

			<td><c:if test="${param.inquiryFlag != 'true'}">
				<div id="lookup" align="center"><br />
				<br />
				<table class="datatable-100" align="center" cellpadding="0"
					cellspacing="0">
					<c:set var="FormName" value="KualiForm" scope="request" />
					<c:set var="FieldRows" value="${KualiForm.lookupable.rows}"
						scope="request" />
					<c:set var="ActionName" value="glModifiedInquiry.do"
						scope="request" />
					<c:set var="IsLookupDisplay" value="true" scope="request" />

					<kul:rowDisplay rows="${KualiForm.lookupable.rows}"
						numberOfColumns="${KualiForm.numColumns}" />

					<tr align=center>
						<td height="30" colspan="${KualiForm.numColumns*2}"
							class="infoline"><html:image property="methodToCall.search"
							value="search"
							src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_search.gif"
							styleClass="tinybutton" alt="search" title="search" border="0" />
						<html:image property="methodToCall.clearValues"
							value="clearValues"
							src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_clear.gif"
							styleClass="tinybutton" alt="clear" title="clear" border="0" />
						<c:if test="${KualiForm.formKey!=''}">
							<a
								href='<c:out value="${KualiForm.backLocation}?methodToCall=refresh&docFormKey=${KualiForm.formKey}" />'
								title="cancel"> <img
								src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_cancel.gif"
								class="tinybutton" border="0" alt="cancel" title="cancel" /> </a>
						</c:if> <!-- Optional extra button --> <c:if
							test="${not empty KualiForm.lookupable.extraButtonSource}">
							<a
								href='<c:out value="${KualiForm.backLocation}?methodToCall=refresh&refreshCaller=org.kuali.rice.kns.lookup.KualiLookupableImpl&docFormKey=${KualiForm.formKey}" /><c:out value="${KualiForm.lookupable.extraButtonParams}" />'>
							<img
								src='<c:out value="${KualiForm.lookupable.extraButtonSource}" />'
								class="tinybutton" border="0" /></a>
						</c:if></td>
					</tr>
				</table>
				</div>

				<br />
				<br />

			</c:if></td>
		</tr>
	</table>
	<table width="100%" cellspacing="0" cellpadding="0">
		<tr>
			<td><c:if test="${!empty reqSearchResultsSize}">
				<%-- If results size isn't the same as the actual size, it
				     means that the result set was truncated (see
				     LookupDaoOjb.executeSearch), so display the total amount
				     of matching records. --%>
				<c:if test="${reqSearchResultsSize != fn:length(reqSearchResults)}">
					<c:out value="${reqSearchResultsSize}" /> items found.
					Please refine your search criteria to narrow down your search.
				</c:if>

				<c:set var="exporting" value="${!empty param['d-16544-e']}"
					scope="request" />
				<display:table class="datatable-100" cellspacing="0" cellpadding="0"
					name="${reqSearchResults}" id="row" export="true" pagesize="100"
					defaultsort="1"
					requestURI="glModifiedInquiry.do?methodToCall=viewResults&reqSearchResultsSize=${reqSearchResultsSize}&searchResultKey=${searchResultKey}">

					<c:forEach items="${row.columns}" var="column">

						<c:if test="${!empty column.columnAnchor.title}">
							<c:set var="title" value="${column.columnAnchor.title}" />
						</c:if>
						<c:if test="${empty column.columnAnchor.title}">
							<c:set var="title" value="${column.propertyValue}" />
						</c:if>
						<c:choose>

							<c:when
								test="${column.formatter.implementationClass == 'org.kuali.rice.core.web.format.CurrencyFormatter'}">

								<display:column class="numbercell" sortable="true"
									decorator="org.kuali.rice.kns.web.ui.FormatAwareDecorator"
									title="${column.columnTitle}" comparator="${column.comparator}">

									<c:choose>

										<c:when test="${column.propertyURL != \"\"}">
											<a href="<c:out value="${column.propertyURL}"/>"
												title="<c:out value="${title}" />" target="blank"><c:out
												value="${column.propertyValue}" /></a>
										</c:when>

										<c:otherwise>
											<c:out value="${column.propertyValue}" />
										</c:otherwise>
									</c:choose>
								</display:column>

							</c:when>

							<c:otherwise>

								<c:choose>

									<c:when test="${column.propertyURL != \"\"}">

										<display:column class="infocell" sortable="${column.sortable}"
											decorator="org.kuali.rice.kns.web.ui.FormatAwareDecorator"
											title="${column.columnTitle}"
											comparator="${column.comparator}">

											<a href="<c:out value="${column.propertyURL}"/>"
												title="<c:out value="${title}" />" target="blank"><c:out
												value="${column.propertyValue}" /></a>

										</display:column>

									</c:when>

									<c:otherwise>

										<display:column class="infocell" sortable="${column.sortable}"
											decorator="org.kuali.rice.kns.web.ui.FormatAwareDecorator"
											title="${column.columnTitle}"
											comparator="${column.comparator}">

											<c:if
												test="${!exporting && column.columnTitle == 'Project Code'}">
												<div style="white-space: nowrap"><c:out
													value="${column.propertyValue}" /></div>
											</c:if>

											<c:if
												test="${exporting || column.columnTitle != 'Project Code'}">
												<c:out value="${column.propertyValue}" />
											</c:if>

										</display:column>

									</c:otherwise>

								</c:choose>

							</c:otherwise>

						</c:choose>

					</c:forEach>
					<c:if test="${param['d-16544-e'] == null}">
						<logic:present name="KualiForm" property="formKey">
							<c:if
								test="${KualiForm.formKey!='' && KualiForm.hideReturnLink!=true && !KualiForm.multipleValues && param.inquiryFlag != 'true'}">
								<display:column class="infocell" property="returnUrl"
									media="html" />
							</c:if>
							<c:if
								test="${row.actionUrls!='' && KualiForm.suppressActions!=true && !KualiForm.multipleValues && KualiForm.showMaintenanceLinks}">
								<display:column class="infocell" property="actionUrls"
									title="Actions" media="html" />
							</c:if>
						</logic:present>
					</c:if>
				</display:table></td>
			</c:if>
			<td width="1%"><img
				src="${ConfigProperties.kr.externalizable.images.url}pixel_clear.gif"
				alt="" height="20" width="20"></td>
				
		</tr>
	</table>
	
</kul:page>
