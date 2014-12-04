<%--
 Copyright 2005-2007 The Kuali Foundation

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
<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>

<%--NOTE: DO NOT FORMAT THIS FILE, DISPLAY:COLUMN WILL NOT WORK CORRECTLY IF IT CONTAINS LINE BREAKS --%>
<c:set var="headerMenu" value="" />
<c:set var="numberOfColumns" value="${KualiForm.numColumns}" />
<c:set var="headerColspan" value="${numberOfColumns * 2}" />


<kul:page lookup="true" showDocumentInfo="false"
	headerMenuBar="${headerMenu}" headerTitle="Lookup" docTitle=""
	transactionalDocument="false"
	htmlFormAction="${KualiForm.htmlFormAction}">

	<SCRIPT type="text/javascript">
		var kualiForm = document.forms['KualiForm'];
		var kualiElements = kualiForm.elements;
		var excludeSubmitRestriction = true;
	</SCRIPT>
	<script type="text/javascript"
		src="${pageContext.request.contextPath}/dwr/interface/DocumentTypeService.js"></script>


	<c:if test="${KualiForm.headerBarEnabled}">
		<div class="headerarea-small" id="headerarea-small">
			<h1>
				<c:out value="${KualiForm.lookupable.title}" />
				<c:choose>
					<c:when test="${KualiForm.fields.docTypeFullName != null}">
						<%-- this is a custom doc search --%>
						<kul:help
							searchDocumentTypeName="${KualiForm.fields.docTypeFullName}"
							altText="lookup help" />
					</c:when>
					<c:otherwise>
						<%-- KNS looup --%>
						<kul:help
							lookupBusinessObjectClassName="${KualiForm.lookupable.businessObjectClass.name}"
							altText="lookup help" />
					</c:otherwise>
				</c:choose>
			</h1>
		</div>
	</c:if>

	<c:if test="${KualiForm.renderSearchButtons}">
		<kul:enterKey methodToCall="search" />
	</c:if>

	<html-el:hidden name="KualiForm" property="backLocation" />
	<html-el:hidden name="KualiForm" property="formKey" />
	<html-el:hidden name="KualiForm" property="lookupableImplServiceName" />
	<html-el:hidden name="KualiForm" property="businessObjectClassName" />
	<html-el:hidden name="KualiForm" property="conversionFields" />
	<html-el:hidden name="KualiForm" property="hideReturnLink" />
	<html-el:hidden name="KualiForm" property="suppressActions" />
	<html-el:hidden name="KualiForm" property="multipleValues" />
	<html-el:hidden name="KualiForm" property="lookupAnchor" />
	<html-el:hidden name="KualiForm" property="readOnlyFields" />
	<html-el:hidden name="KualiForm" property="referencesToRefresh" />
	<html-el:hidden name="KualiForm" property="hasReturnableRow" />
	<html-el:hidden name="KualiForm" property="docNum" />
	<html-el:hidden name="KualiForm" property="showMaintenanceLinks" />
	<html-el:hidden name="KualiForm" property="headerBarEnabled" />
	<html-el:hidden name="KualiForm"
		property="fieldNameToFocusOnAfterSubmit" />

	<c:if test="${KualiForm.headerBarEnabled}">
		<c:forEach items="${KualiForm.extraButtons}" varStatus="status">
			<html-el:hidden name="KualiForm"
				property="extraButtons[${status.index}].extraButtonSource" />
			<html-el:hidden name="KualiForm"
				property="extraButtons[${status.index}].extraButtonParams" />
		</c:forEach>
		<c:if test="${KualiForm.supplementalActionsEnabled==true}">
			<div class="lookupcreatenew" title="Supplemental Search Actions"
				style="padding: 3px 30px 3px 300px;">
				${KualiForm.lookupable.supplementalMenuBar} &nbsp;
				<c:set var="extraField" value="${KualiForm.lookupable.extraField}" />
				<c:if test="${not empty extraField}">
					<%--has to be a dropdown script for now--%>
					<c:if test="${extraField.fieldType eq extraField.DROPDOWN_SCRIPT}">

                            	${kfunc:registerEditableProperty(KualiForm, extraField.propertyName)}
                                <select id='${extraField.propertyName}'
							name='${extraField.propertyName}' onchange="${extraField.script}"
							style="">
							<kul:fieldSelectValues field="${extraField}" />
						</select>

						&nbsp;

							<kul:fieldShowIcons isReadOnly="${true}" field="${extraField}"
							addHighlighting="${true}" />

					</c:if>
				</c:if>
			</div>
		</c:if>

		<div class="right">
			<div class="excol">* required field</div>
		</div>
		<div class="msg-excol">
			<div class="left-errmsg">
				<kul:errors errorTitle="Errors found in Search Criteria:" />
				<kul:messages />
			</div>
		</div>
		<br />
	</c:if>

	<table width="100%">
		<c:if test="${KualiForm.lookupCriteriaEnabled}">
			<tr>
				<td width="1%"><img
					src="${ConfigProperties.kr.externalizable.images.url}pixel_clear.gif"
					alt="" width="20" height="20"></td>
				<td>

					<div id="lookup" align="center">
						<br /> <br />
						<table align="center" cellpadding="0" cellspacing="0"
							class="datatable-100">
							<c:set var="FormName" value="KualiForm" scope="request" />
							<c:set var="FieldRows" value="${KualiForm.lookupable.rows}"
								scope="request" />
							<c:set var="ActionName" value="${KualiForm.htmlFormAction}"
								scope="request" />
							<c:set var="IsLookupDisplay" value="true" scope="request" />
							<c:set var="cellWidth" value="50%" scope="request" />

							<kul:rowDisplay rows="${FieldRows}" skipTheOldNewBar="true"
								numberOfColumns="${numberOfColumns}" />

							<tr align="center">
								<td height="30" colspan="${headerColspan}" class="infoline">

									<c:if test="${KualiForm.renderSearchButtons}">
										<html:image property="methodToCall.search" value="search"
											src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_search.gif"
											styleClass="tinybutton" alt="search" title="search"
											border="0" />
										<html:image property="methodToCall.clearValues"
											value="clearValues"
											src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_clear.gif"
											styleClass="tinybutton" alt="clear" title="clear" border="0" />
									</c:if> <c:if test="${KualiForm.formKey!=''}">
										<c:if test="${!empty KualiForm.backLocation}">
											<a
												href='<c:out value="${KualiForm.backLocation}?methodToCall=refresh&docFormKey=${KualiForm.formKey}&anchor=${KualiForm.lookupAnchor}&docNum=${KualiForm.docNum}" />'
												title="cancel"><img
												src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_cancel.gif"
												class="tinybutton" alt="cancel" title="cancel" border="0" />
											</a>
										</c:if>
										</c:if> <!-- Optional extra buttons --> <c:set var="extraButtons"
										value="${KualiForm.extraButtons}" />
									<div id="globalbuttons" class="globalbuttons">
										<c:if test="${!empty extraButtons}">
											<c:forEach items="${extraButtons}" var="extraButton">
												<html:image src="${extraButton.extraButtonSource}"
													styleClass="globalbuttons"
													property="${extraButton.extraButtonProperty}"
													title="${extraButton.extraButtonAltText}"
													alt="${extraButton.extraButtonAltText}" />
											</c:forEach>
										</c:if>
									</div> <c:if
										test="${KualiForm.multipleValues && !empty KualiForm.backLocation}">
										<a
											href='<c:out value="${KualiForm.backLocation}?methodToCall=refresh&docFormKey=${KualiForm.formKey}&anchor=${KualiForm.lookupAnchor}&docNum=${KualiForm.docNum}" />'>
											<img
											src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_retnovalue.gif"
											class="tinybutton" border="0" /> </a>
										<a
											href='<c:out value="${KualiForm.backLocation}?methodToCall=refresh&docFormKey=${KualiForm.formKey}&refreshCaller=multipleValues&searchResultKey=${searchResultKey}&searchResultDataKey=${searchResultDataKey}&anchor=${KualiForm.lookupAnchor}&docNum=${KualiForm.docNum}"/>'>
											<img
											src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_returnthese.gif"
											class="tinybutton" border="0" /> </a>
									</c:if>
								</td>
							</tr>
							</c:if>
						</table>
					</div> <br> <br> <c:if test="${reqSearchResultsActualSize>0}">
						<c:out value="${reqSearchResultsActualSize}" /> items found.  Please refine your search criteria to narrow down your search.
            </c:if> <c:if test="${!empty reqSearchResultsActualSize }">
						<c:if test="${KualiForm.searchUsingOnlyPrimaryKeyValues}">
							<bean-el:message key="lookup.using.primary.keys"
								arg0="${KualiForm.primaryKeyFieldLabels}" />
							<br />
							<br />
						</c:if>
						<c:if
							test="${!empty reqSearchResults && !KualiForm.hasReturnableRow && KualiForm.formKey!='' && KualiForm.hideReturnLink!=true && !KualiForm.multipleValues}">
							<bean-el:message key="lookup.no.returnable.rows" />
							<br />
							<br />
						</c:if>

						<display:table class="datatable-100" cellspacing="0"
							requestURIcontext="false" cellpadding="0"
							name="${reqSearchResults}" id="row" export="true" pagesize="100"
							varTotals="totals"
							excludedParams="methodToCall reqSearchResultsActualSize searchResultKey searchUsingOnlyPrimaryKeyValues actionUrlsExist"
							requestURI="${KualiForm.htmlFormAction}.do?methodToCall=viewResults&reqSearchResultsActualSize=${reqSearchResultsActualSize}&searchResultKey=${searchResultKey}&searchUsingOnlyPrimaryKeyValues=${KualiForm.searchUsingOnlyPrimaryKeyValues}&actionUrlsExist=${KualiForm.actionUrlsExist}">
							
							<c:if test="${KualiForm.displayActionsForRow}">					
								<logic:present name="KualiForm" property="formKey">
									<c:choose>
										<c:when test="${row.actionUrls!=''}">
											<display:column class="infocell" property="actionUrls" title="Actions" media="html" />
										</c:when>
										<c:otherwise>
											<display:column class="infocell" title="Actions" media="html">&nbsp;</display:column>
										</c:otherwise>
									</c:choose>
								</logic:present>
							</c:if>

							<%-- needed for total line display --%>
							<c:set var="columnNums" value="" />
							<c:set var="totalColumnNums" value="" />

							<c:forEach items="${row.columns}" var="column"
								varStatus="loopStatus">
								<c:set var="colClass"
									value="${ fn:startsWith(column.formatter, 'org.kuali.rice.core.web.format.CurrencyFormatter') ? 'numbercell' : 'infocell' }" />

								<c:if test="${!empty columnNums}">
									<c:set var="columnNums" value="${columnNums}," />
								</c:if>
								<c:set var="columnNums"
									value="${columnNums}column${loopStatus.count}" />

								<c:set var="staticColumnValue" value="${column.propertyValue}" />
								<c:if test="${column.total}">
									<c:set var="staticColumnValue"
										value="${column.unformattedPropertyValue}" />

									<c:if test="${!empty totalColumnNums}">
										<c:set var="totalColumnNums" value="${totalColumnNums}," />
									</c:if>
									<c:set var="totalColumnNums"
										value="${totalColumnNums}column${loopStatus.count}" />
								</c:if>

								<c:choose>
									<%--NOTE: Check if exporting first, as this should be outputted without extra HTML formatting --%>
									<c:when test="${param['d-16544-e'] != null}">
										<display:column class="${colClass}"
											sortable="${column.sortable}" title="${column.columnTitle}"
											comparator="${column.comparator}" total="${column.total}"
											value="${staticColumnValue}" maxLength="${column.maxLength}">
											<c:out value="${column.propertyValue}" escapeXml="false"
												default="" />
										</display:column>
									</c:when>
									<c:when
										test="${!empty column.columnAnchor.href || column.multipleAnchors}">
										<display:column class="${colClass}"
											sortable="${column.sortable}" title="${column.columnTitle}"
											comparator="${column.comparator}">
											<c:choose>
												<c:when test="${column.multipleAnchors}">
													<c:set var="numberOfColumnAnchors"
														value="${column.numberOfColumnAnchors}" />
													<c:choose>
														<c:when test="${empty columnAnchor.target}">
															<c:set var="anchorTarget" value="_blank" />
														</c:when>
														<c:otherwise>
															<c:set var="anchorTarget" value="${columnAnchor.target}" />
														</c:otherwise>
													</c:choose>
													<!-- Please don't change formatting of this logic:iterate block -->
													<logic:iterate id="columnAnchor" name="column"
														property="columnAnchors" indexId="ctr">
														<a href="<c:out value="${columnAnchor.href}"/>"
															target='<c:out value="${columnAnchor.target}"/>'
															title="${columnAnchor.title}"><c:out
																value="${fn:substring(columnAnchor.displayText, 0, column.maxLength)}"
																escapeXml="${column.escapeXMLValue}" /> <c:if
																test="${column.maxLength gt 0 && fn:length(columnAnchor.displayText) gt column.maxLength}">...</c:if>
														</a>
														<c:if test="${ctr lt numberOfColumnAnchors-1}">,&nbsp;</c:if>
													</logic:iterate>
												</c:when>
												<c:otherwise>
													<c:choose>
														<c:when test="${empty column.columnAnchor.target}">
															<c:set var="anchorTarget" value="_blank" />
														</c:when>
														<c:otherwise>
															<c:set var="anchorTarget"
																value="${column.columnAnchor.target}" />
														</c:otherwise>
													</c:choose>
													<a href="<c:out value="${column.columnAnchor.href}"/>"
														target='<c:out value="${anchorTarget}"/>'
														title="${column.columnAnchor.title}"><c:out
															value="${fn:substring(column.propertyValue, 0, column.maxLength)}"
															escapeXml="${column.escapeXMLValue}" /> <c:if
															test="${column.maxLength gt 0 && fn:length(column.propertyValue) gt column.maxLength}">...</c:if>
													</a>
												</c:otherwise>
											</c:choose>
										</display:column>
									</c:when>
									<%--NOTE: DO NOT FORMAT THIS FILE, DISPLAY:COLUMN WILL NOT WORK CORRECTLY IF IT CONTAINS LINE BREAKS --%>
									<c:otherwise>
										<display:column class="${colClass}"
											sortable="${column.sortable}" title="${column.columnTitle}"
											comparator="${column.comparator}" total="${column.total}"
											value="${staticColumnValue}" maxLength="${column.maxLength}"
											decorator="org.kuali.rice.kns.web.ui.FormatAwareDecorator">
											<c:out value="${column.propertyValue}" />&nbsp;</display:column>
									</c:otherwise>
								</c:choose>
							</c:forEach>

							<%-- block for displaying the total line --%>
							<c:if test="${!empty totalColumnNums}">
								<display:footer>
									<tr>
										<c:forTokens var="colNum" items="${columnNums}" delims=","
											varStatus="loopStatus">
											<c:set var="isTotalColumn" value="false" />

											<c:forTokens var="totalNum" items="${totalColumnNums}"
												delims=",">
												<c:if test="${totalNum eq colNum}">
													<c:set var="isTotalColumn" value="true" />
												</c:if>
											</c:forTokens>

											<c:choose>
												<c:when test="${isTotalColumn}">
													<td><b><fmt:formatNumber type="currency"
																value="${totals[colNum]}" /> </b></td>
												</c:when>
												<c:otherwise>
													<td><c:if test="${loopStatus.first}">
															<b><bean-el:message key="lookup.total.row.label" />
															</b>
														</c:if>
													</td>
												</c:otherwise>
											</c:choose>

										</c:forTokens>
									</tr>
								</display:footer>
							</c:if>

						</display:table>
					</c:if></td>
				<td width="1%"><img
					src="${ConfigProperties.kr.externalizable.images.url}pixel_clear.gif"
					alt="" width="20" height="20"></td>
			</tr>
	</table>
</kul:page>
