<%@ include file="/jsp/core/tldHeader.jsp"%>

<kul:pageLookup showDocumentInfo="false"
	htmlFormAction="glBalanceInquiry"
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
			<td width="1%"><img src="images/pixel_clear.gif" alt="" width="20"
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
								border="0" /></a>
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
			</c:if> <c:if test="${param.inquiryFlag == 'true'}">
				<c:set var="url" value="${pageContext.request.requestURL}" />

				<c:url value="${url}" var="amountViewSwitch">
					<c:forEach items="${param}" var="params">
						<c:if
							test="${params.key == 'dummyBusinessObject.amountViewOption'}">
							<c:if test="${params.value == 'Accumulate' }">
								<c:param name="${params.key}" value="Monthly" />
								<c:set var="amountViewLabel" value="View Monthly Amount" />
							</c:if>
							<c:if test="${params.value != 'Accumulate' }">
								<c:param name="${params.key}" value="Accumulate" />
								<c:set var="amountViewLabel" value="View Accumulate Amount" />
							</c:if>
						</c:if>

						<c:if
							test="${params.key != 'dummyBusinessObject.amountViewOption'}">
							<c:param name="${params.key}" value="${params.value}" />
						</c:if>
					</c:forEach>
				</c:url>

				<a href="<c:out value='${amountViewSwitch}'/>"> <input type="button"
					name="amountViewSwitch" value="<c:out value='${amountViewLabel}'/>" />
				</a>
			</c:if> <br />
			<br />

			<c:if test="${reqSearchResultsActualSize>0}">
				<c:out value="${reqSearchResultsActualSize}" /> items found.
	          	</c:if> <display:table class="datatable-100" cellspacing="0"
				cellpadding="0" name="${reqSearchResults[0]}" id="dummyRow"
				export="false" pagesize="1" length="1" summary=""
				requestURI="glBalanceInquiry.do?methodToCall=viewResults&reqSearchResultsActualSize=${reqSearchResultsActualSize}&searchResultKey=${searchResultKey}">

				<display-el:setProperty name="paging.banner.one_item_found" value="" />

				<display:column class="infocell">
					<table width="100%" class="datatable-100" cellspacing="0"
						cellpadding="0" id="row">
						<c:forEach items="${reqSearchResults}" var="row"
							varStatus="status">
							<!-- c:if test="${status.count == 1}" -->
							<tr>
								<c:forEach items="${row.columns}" var="column" end="11">
									<th class="infocell"><c:out value="${column.columnTitle}" /></th>
								</c:forEach>
							</tr>
							<!-- /c:if -->

							<tr>
								<c:forEach items="${row.columns}" var="column" end="7">
									<c:if
										test="${column.propertyURL!=\"\" && param['d-16544-e'] == null}">
										<td class="infocell"><a
											href="<c:out value="${column.propertyURL}"/>" target="blank">
										<c:out value="${column.propertyValue}" /> </a></td>
									</c:if>
									<c:if
										test="${column.propertyURL==\"\" || param['d-16544-e'] != null}">
										<td class="infocell"><c:out value="${column.propertyValue}" />
										</td>
									</c:if>
								</c:forEach>
								<c:forEach items="${row.columns}" var="column" begin="8"
									end="11">
									<c:if
										test="${column.propertyURL!=\"\" && param['d-16544-e'] == null}">
										<td class="numbercell"><a
											href="<c:out value="${column.propertyURL}"/>" target="blank">
										<c:out value="${column.propertyValue}" /> </a></td>
									</c:if>
									<c:if
										test="${column.propertyURL==\"\" || param['d-16544-e'] != null}">
										<td class="numbercell"><c:out value="${column.propertyValue}" />
										</td>
									</c:if>
								</c:forEach>
							</tr>

							<tr>
								<td colspan="12" class="infocell"><br>
								<center>
								<table class="datatable-80" cellspacing="0" cellpadding="0"
									id="row">
									<tr>
										<th class="infocell" width="10%"><c:out
											value="${row.columns[12].columnTitle}" /></th>
										<td class="numbercell" width="15%"><a
											href="<c:out value="${row.columns[12].propertyURL}"/>"
											target="blank"> <c:out
											value="${row.columns[12].propertyValue}" /> </a></td>
										<th class="infocell" width="10%"><c:out
											value="${row.columns[15].columnTitle}" /></th>
										<td class="numbercell" width="15%"><a
											href="<c:out value="${row.columns[15].propertyURL}"/>"
											target="blank"> <c:out
											value="${row.columns[15].propertyValue}" /> </a></td>
										<th class="infocell" width="10%"><c:out
											value="${row.columns[18].columnTitle}" /></th>
										<td class="numbercell" width="15%"><a
											href="<c:out value="${row.columns[18].propertyURL}"/>"
											target="blank"> <c:out
											value="${row.columns[18].propertyValue}" /> </a></td>
										<th class="infocell" width="10%"><c:out
											value="${row.columns[21].columnTitle}" /></th>
										<td class="numbercell" width="15%"><a
											href="<c:out value="${row.columns[21].propertyURL}"/>"
											target="blank"> <c:out
											value="${row.columns[21].propertyValue}" /> </a></td>
									</tr>
									<tr>
										<th class="infocell" width="10%"><c:out
											value="${row.columns[13].columnTitle}" /></th>
										<td class="numbercell" width="15%"><a
											href="<c:out value="${row.columns[13].propertyURL}"/>"
											target="blank"> <c:out
											value="${row.columns[13].propertyValue}" /> </a></td>
										<th class="infocell" width="10%"><c:out
											value="${row.columns[16].columnTitle}" /></th>
										<td class="numbercell" width="15%"><a
											href="<c:out value="${row.columns[16].propertyURL}"/>"
											target="blank"> <c:out
											value="${row.columns[16].propertyValue}" /> </a></td>
										<th class="infocell" width="10%"><c:out
											value="${row.columns[19].columnTitle}" /></th>
										<td class="numbercell" width="15%"><a
											href="<c:out value="${row.columns[19].propertyURL}"/>"
											target="blank"> <c:out
											value="${row.columns[19].propertyValue}" /> </a></td>
										<th class="infocell" width="10%"><c:out
											value="${row.columns[22].columnTitle}" /></th>
										<td class="numbercell" width="15%"><a
											href="<c:out value="${row.columns[22].propertyURL}"/>"
											target="blank"> <c:out
											value="${row.columns[22].propertyValue}" /> </a></td>
									</tr>
									<tr>
										<th class="infocell" width="10%"><c:out
											value="${row.columns[14].columnTitle}" /></th>
										<td class="numbercell" width="15%"><a
											href="<c:out value="${row.columns[14].propertyURL}"/>"
											target="blank"> <c:out
											value="${row.columns[14].propertyValue}" /> </a></td>
										<th class="infocell" width="10%"><c:out
											value="${row.columns[17].columnTitle}" /></th>
										<td class="numbercell" width="15%"><a
											href="<c:out value="${row.columns[17].propertyURL}"/>"
											target="blank"> <c:out
											value="${row.columns[17].propertyValue}" /> </a></td>
										<th class="infocell" width="10%"><c:out
											value="${row.columns[20].columnTitle}" /></th>
										<td class="numbercell" width="15%"><a
											href="<c:out value="${row.columns[20].propertyURL}"/>"
											target="blank"> <c:out
											value="${row.columns[20].propertyValue}" /> </a></td>
										<th class="infocell" width="10%"><c:out
											value="${row.columns[23].columnTitle}" /></th>
										<td class="numbercell" width="15%"><a
											href="<c:out value="${row.columns[23].propertyURL}"/>"
											target="blank"> <c:out
											value="${row.columns[23].propertyValue}" /> </a></td>
									</tr>
								</table>
								</center>
								<p>&nbsp;</p>
								</td>
							</tr>
						</c:forEach>
					</table>
				</display:column>
			</display:table></td>
			<td width="1%"><img src="images/pixel_clear.gif" alt="" height="20"
				width="20"></td>
		</tr>
	</table>

</kul:pageLookup>
