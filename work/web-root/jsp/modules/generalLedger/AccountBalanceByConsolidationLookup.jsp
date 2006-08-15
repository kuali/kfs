<%@ include file="/jsp/core/tldHeader.jsp"%>

<kul:pageLookup showDocumentInfo="false"
	htmlFormAction="glAccountBalanceByConsolidationLookup"
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
			<td>
			<div id="lookup" align="center"><br />
			<br />
			<table class="datatable-100" align="center" cellpadding="0"
				cellspacing="0">
				<c:set var="FormName" value="KualiForm" scope="request" />
				<c:set var="FieldRows" value="${KualiForm.lookupable.rows}"
					scope="request" />
				<c:set var="ActionName" value="glModifiedInquiry.do" scope="request" />
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

			<c:set var="offset" value="8" /> <display:table class="datatable-100"
				cellspacing="0" cellpadding="0" name="${reqSearchResults}" id="row"
				export="true" pagesize="100" offset="${offset}"
				requestURI="glAccountBalanceByConsolidationLookup.do?methodToCall=viewResults&reqSearchResultsActualSize=${reqSearchResultsActualSize}&searchResultKey=${searchResultKey}">
				<c:forEach items="${row.columns}" var="column" varStatus="status">
					<c:if
						test="${column.propertyURL!=\"\" && param['d-16544-e'] == null}">
						<display:column class="infocell" title="${column.columnTitle}">
							<a href="<c:out value="${column.propertyURL}"/>" target="blank">
							<c:out value="${column.propertyValue}" /> </a>
						</display:column>
					</c:if>

					<c:if
						test="${(column.propertyURL==\"\" || param['d-16544-e'] != null)}">
						<c:if
							test="${column.formatter.implementationClass == 'org.kuali.core.web.format.CurrencyFormatter'}">
							<display:column class="numbercell" title="${column.columnTitle}">
								<c:out value="${column.propertyValue}" />
							</display:column>
						</c:if>
						<c:if
							test="${column.formatter.implementationClass != 'org.kuali.core.web.format.CurrencyFormatter'}">
							<display:column class="infocell" title="${column.columnTitle}">
								<c:out value="${column.propertyValue}" />
							</display:column>
						</c:if>
					</c:if>
				</c:forEach>
			</display:table>

			<c:if test="${reqSearchResultsActualSize>7}">
				<div style="float: right; width: 70%;"><br />
				<br />
                <table class="datatable-100" id="row" cellpadding="0" cellspacing="0">
                  <caption style="text-align: left; font-weight: bold;">Totals</caption>
                  <thead>
                    <tr>
                      <th>Type</th>
                      <th>Budget Amount</th>
                      <th>Actuals Amount</th>
                      <th>Encumbrance Amount</th>
                      <th>Variance</th>
                    </tr>
                  </thead>
                  <tfoot>
                    <tr>
                      <th colspan="4" class="infocell" style="text-align: right;">${reqSearchResults[6].columns[5].propertyValue}</th>
                      <td class="numbercell">${reqSearchResults[6].columns[9].propertyValue}</td>
                    </tr>
                  </tfoot>
                  <tbody>
                    <tr class="odd">
                      <td class="infocell">${reqSearchResults[0].columns[5].propertyValue}</td>
                      <td class="numbercell">${reqSearchResults[0].columns[6].propertyValue}</td>
                      <td class="numbercell">${reqSearchResults[0].columns[7].propertyValue}</td>
                      <td class="numbercell">${reqSearchResults[0].columns[8].propertyValue}</td>
                      <td class="numbercell">${reqSearchResults[0].columns[9].propertyValue}</td>
                    </tr>
                    <tr class="odd">
                      <td class="infocell">${reqSearchResults[1].columns[5].propertyValue}</td>
                      <td class="numbercell">${reqSearchResults[1].columns[6].propertyValue}</td>
                      <td class="numbercell">${reqSearchResults[1].columns[7].propertyValue}</td>
                      <td class="numbercell">${reqSearchResults[1].columns[8].propertyValue}</td>
                      <td class="numbercell">${reqSearchResults[1].columns[9].propertyValue}</td>
                    </tr>
                    <tr class="even">
                      <td class="infocell"><b>${reqSearchResults[2].columns[5].propertyValue}</b></td>
                      <td class="numbercell">${reqSearchResults[2].columns[6].propertyValue}</td>
                      <td class="numbercell">${reqSearchResults[2].columns[7].propertyValue}</td>
                      <td class="numbercell">${reqSearchResults[2].columns[8].propertyValue}</td>
                      <td class="numbercell">${reqSearchResults[2].columns[9].propertyValue}</td>
                    </tr>
                    <tr class="odd">
                      <td class="infocell" colspan="5">&nbsp;</td>
                    </tr>
                    <tr class="odd">
                      <td class="infocell">${reqSearchResults[3].columns[5].propertyValue}</td>
                      <td class="numbercell">${reqSearchResults[3].columns[6].propertyValue}</td>
                      <td class="numbercell">${reqSearchResults[3].columns[7].propertyValue}</td>
                      <td class="numbercell">${reqSearchResults[3].columns[8].propertyValue}</td>
                      <td class="numbercell">${reqSearchResults[3].columns[9].propertyValue}</td>
                    </tr>
                    <tr class="odd">
                      <td class="infocell">${reqSearchResults[4].columns[5].propertyValue}</td>
                      <td class="numbercell">${reqSearchResults[4].columns[6].propertyValue}</td>
                      <td class="numbercell">${reqSearchResults[4].columns[7].propertyValue}</td>
                      <td class="numbercell">${reqSearchResults[4].columns[8].propertyValue}</td>
                      <td class="numbercell">${reqSearchResults[4].columns[9].propertyValue}</td>
                    </tr>
                    <tr class="even">
                      <td class="infocell"><b>${reqSearchResults[5].columns[5].propertyValue}</b></td>
                      <td class="numbercell">${reqSearchResults[5].columns[6].propertyValue}</td>
                      <td class="numbercell">${reqSearchResults[5].columns[7].propertyValue}</td>
                      <td class="numbercell">${reqSearchResults[5].columns[8].propertyValue}</td>
                      <td class="numbercell">${reqSearchResults[5].columns[9].propertyValue}</td>
                    </tr>
                    <tr class="odd">
                      <td class="infocell" colspan="5">&nbsp;</td>
                    </tr>
                  </tbody>
                </table>
				</div>
			</c:if></td>
			<td width="1%"><img src="images/pixel_clear.gif" alt="" height="20"
				width="20"></td>
		</tr>
	</table>
	<br />
	<br />
</kul:pageLookup>
