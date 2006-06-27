<%@ include file="/jsp/core/tldHeader.jsp"%>

<kul:pageLookup showDocumentInfo="false"
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

	<table width="100%" cellspacing="0" cellpadding="0">
		<tr>
			<td width="1%"><img src="images/pixel_clear.gif" alt="" width="20"
				height="20" /></td>

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

					<%@ include file="/jsp/core/RowDisplay.jsp"%>

					<tr align=center>
						<td height="30" colspan=2 class="infoline"><html:image
							property="methodToCall.search" value="search"
							src="images/buttonsmall_search.gif" styleClass="tinybutton"
							alt="search" border="0" /> <html:image
							property="methodToCall.clearValues" value="clearValues"
							src="images/buttonsmall_clear.gif" styleClass="tinybutton"
							alt="clear" border="0" /> <c:if test="${KualiForm.formKey!=''}">
							<a
								href='<c:out value="${KualiForm.backLocation}?methodToCall=refresh&docFormKey=${KualiForm.formKey}" />'>
							<img src="images/buttonsmall_cancel.gif" class="tinybutton"
								border="0" /> </a>
						</c:if> <!-- Optional extra button --> <c:if
							test="${not empty KualiForm.lookupable.extraButtonSource}">
							<a
								href='<c:out value="${KualiForm.backLocation}?methodToCall=refresh&refreshCaller=org.kuali.core.lookup.KualiLookupableImpl&docFormKey=${KualiForm.formKey}" /><c:out value="${KualiForm.lookupable.extraButtonParams}" />'>
							<img
								src='<c:out value="${KualiForm.lookupable.extraButtonSource}" />'
								class="tinybutton" border="0" /></a>
						</c:if></td>
					</tr>
				</table>
				</div>

				<br />
				<br />

				<div class="right"><logic-el:present name="KualiForm"
					property="formKey">
					<c:if
						test="${KualiForm.formKey!='' && KualiForm.hideReturnLink != true}">
						<a
							href='<c:out value="${KualiForm.backLocation}?methodToCall=refresh&docFormKey=${KualiForm.formKey}" />'>
						return with no value </a>
					</c:if>
				</logic-el:present></div>
			</c:if> <c:if test="${reqSearchResultsActualSize>0}">

				<c:out value="${reqSearchResultsActualSize}" /> items found.  
					
	      		</c:if> <display:table class="datatable-100" cellspacing="0"
				cellpadding="0" name="${reqSearchResults}" id="row" export="true"
				pagesize="100" defaultsort="1"
				requestURI="glModifiedInquiry.do?methodToCall=viewResults&reqSearchResultsActualSize=${reqSearchResultsActualSize}&searchResultKey=${searchResultKey}">

				<c:forEach items="${row.columns}" var="column">

					<c:choose>

						<c:when
							test="${column.formatter.implementationClass == 'org.kuali.core.web.format.CurrencyFormatter'}">

							<display:column class="numbercell" sortable="true"
								decorator="org.kuali.core.web.uidraw.FormatAwareDecorator"
								title="${column.columnTitle}" comparator="${column.comparator}">
								
								<c:choose>

									<c:when test="${column.propertyURL != \"\"}">
											<a href="<c:out value="${column.propertyURL}"/>"
												target="blank"><c:out value="${column.propertyValue}" /></a>	
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
										decorator="org.kuali.core.web.uidraw.FormatAwareDecorator"
										title="${column.columnTitle}"
										comparator="${column.comparator}">

										<a href="<c:out value="${column.propertyURL}"/>"
											target="blank"><c:out value="${column.propertyValue}" /></a>

									</display:column>

								</c:when>

								<c:otherwise>

									<display:column class="infocell" sortable="${column.sortable}"
										decorator="org.kuali.core.web.uidraw.FormatAwareDecorator"
										title="${column.columnTitle}"
										comparator="${column.comparator}">

										<c:if test="${column.columnTitle == 'Project Code'}">
											<div style="white-space: nowrap"><c:out
												value="${column.propertyValue}" /></div>
										</c:if>

										<c:if test="${column.columnTitle != 'Project Code'}">
											<c:out value="${column.propertyValue}" />
										</c:if>

									</display:column>

								</c:otherwise>

							</c:choose>

						</c:otherwise>

					</c:choose>

				</c:forEach>

			</display:table></td>
			<td width="1%"><img src="images/pixel_clear.gif" alt="" height="20"
				width="20"></td>
		</tr>
	</table>
</kul:pageLookup>
