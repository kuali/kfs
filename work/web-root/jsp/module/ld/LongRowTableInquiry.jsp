<%--
 Copyright 2007 The Kuali Foundation
 
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
	htmlFormAction="laborLongRowTableInquiry"
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

	<table width="100%">
		<tr>
			<td width="1%"><img src="${ConfigProperties.kr.externalizable.images.url}pixel_clear.gif" alt="" width="20"
				height="20"></td>

			<td><c:if test="${param.inquiryFlag != 'true'}">
				<div id="lookup" align="center"><br />
				<br />
				<table class="datatable-100" align="center" cellpadding="0"
					cellspacing="0">
					<c:set var="FormName" value="KualiForm" scope="request" />
					<c:set var="FieldRows" value="${KualiForm.lookupable.rows}"
						scope="request" />
					<c:set var="ActionName" value="glBalanceInquiry.do" scope="request" />
					<c:set var="IsLookupDisplay" value="true" scope="request" />

					<kul:rowDisplay rows="${FieldRows}" />

					<tr align=center>
						<td height="30" colspan=2 class="infoline">
							<html:image	property="methodToCall.search" value="search"
										src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_search.gif" styleClass="tinybutton"
										alt="search" title="search" border="0" />
							<html:image	property="methodToCall.clearValues" value="clearValues"
										src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_clear.gif" styleClass="tinybutton"
										alt="clear" title="clear" border="0" />
							<html:image	property="methodToCall.cancel" value="cancel" 
										src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_cancel.gif" styleClass="tinybutton" 
										alt="cancel" title="cancel" border="0" />
							<!--   
							<c:if test="${KualiForm.formKey!=''}">
							<a
								href='<c:out value="${KualiForm.backLocation}?methodToCall=refresh&docFormKey=${KualiForm.formKey}" />' title="cancel">
							<img src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_cancel.gif" class="tinybutton"
								border="0" alt="cancel" title="cancel" /></a>
						    </c:if> <!-- Optional extra button --> 
						    -->
						    
						    <c:if test="${not empty KualiForm.lookupable.extraButtonSource}">
							<a
								href='<c:out value="${KualiForm.backLocation}?methodToCall=refresh&refreshCaller=org.kuali.rice.kns.lookup.KualiLookupableImpl&docFormKey=${KualiForm.formKey}" /><c:out value="${KualiForm.lookupable.extraButtonParams}" />'>
							<img
								src='<c:out value="${KualiForm.lookupable.extraButtonSource}" />'
								class="tinybutton" border="0" alt="Cancel"/></a>
						    </c:if></td>
					</tr>
				</table>
				</div>

				<br />
				<br />
			</c:if> 
			
			<br />
			<br />

			<c:if test="${reqSearchResultsActualSize>0}">
				<c:out value="${reqSearchResultsActualSize}" /> items found.
	        </c:if>
	        
	        <display:table class="datatable-100" cellspacing="0"
				cellpadding="0" name="${reqSearchResults}" id="row"
				export="true" pagesize="100" defaultsort="1" decorator="org.kuali.kfs.module.ld.businessobject.lookup.LongRowTableDecorator"
				requestURI="laborLongRowTableInquiry.do?methodToCall=viewResults&reqSearchResultsActualSize=${reqSearchResultsActualSize}&searchResultKey=${searchResultKey}">
				
				<c:set var="columnLength" value="14" />
				<c:forEach items="${row.columns}" var="column" varStatus="status">
					
					<c:choose>
						<c:when test="${column.formatter.implementationClass == 'org.kuali.rice.core.web.format.CurrencyFormatter'}">
							<display:column class="numbercell" media="${(status.index < columnLength) ? 'all' : 'csv excel xml'}"
								decorator="org.kuali.rice.kns.web.ui.FormatAwareDecorator"
								comparator="${column.comparator}" title="${column.columnTitle}" sortable="true">
									<c:if test="${column.propertyURL != ''}">
											<a href="<c:out value="${column.propertyURL}"/>" title="${column.propertyValue}"
												target="blank"><c:out value="${column.propertyValue}" /></a>	
									</c:if>
									
									<c:if test="${column.propertyURL == ''}"><c:out value="${column.propertyValue}" /></c:if>								
							</display:column>
						</c:when>

						<c:otherwise>
							<c:if test="${column.propertyURL != ''}">
								<display:column class="infocell"
									decorator="org.kuali.rice.kns.web.ui.FormatAwareDecorator"
									media="${(status.index < columnLength) ? 'all' : 'csv excel xml'}"
									comparator="${column.comparator}" title="${column.columnTitle}" sortable="true">

									<a href="<c:out value="${column.propertyURL}"/>" title="${column.propertyValue}"
										target="blank"><c:out value="${column.propertyValue}" /></a>

								</display:column>								
							</c:if>
							
							<c:if test="${column.propertyURL == ''}">								
								<display:column class="infocell"
									decorator="org.kuali.rice.kns.web.ui.FormatAwareDecorator"
									media="${(status.index < columnLength) ? 'all' : 'csv excel xml'}"
									comparator="${column.comparator}" title="${column.columnTitle}" sortable="true">
									
									<c:if test="${column.columnTitle == 'Project Code'}">
										<div style="white-space: nowrap"><c:out
											value="${column.propertyValue}" /></div>
									</c:if>
									
									<c:if test="${column.columnTitle != 'Project Code'}">
										<c:out value="${column.propertyValue}" />
									</c:if>

								</display:column>
							</c:if>
						</c:otherwise>
					</c:choose>					
				</c:forEach>				
			</display:table>
			</td>
			<td width="1%"><img src="${ConfigProperties.kr.externalizable.images.url}pixel_clear.gif" alt="" height="20"
				width="20"></td>
		</tr>
	</table>

</kul:page>
