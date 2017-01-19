<!--  KATTS-1961 Tag and JSP for DV, PREQ and CM Documents -->
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<%@ attribute name="editingMode" required="true" type="java.util.Map"%>
<%@ attribute name="incomeTypeAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for the income type fields." %>

<c:set var="canViewIncomeTypeTab" value="${editingMode['viewIncomeTypes']}" scope="request" />
<c:set var="canEditIncomeType" value="${editingMode['editIncomeTypes']}" scope="request" />

<kul:tab tabTitle="1099 Classification" tabErrorKey="newIncomeTypeError*" hidden="${!canViewIncomeTypeTab}" defaultOpen="${canEditIncomeType}">
  <div class="tab-container" align=center>
    <div style="font-weight:bold;font-size:105%;padding-bottom:5px;">Calendar Year: ${KualiForm.document.paidYear}   (based on paid date)</div>
    <c:if test="${canEditIncomeType}">
      <div align="center">
        <html:image property="methodToCall.refreshIncomeTypesFromAccountLines" src="${ConfigProperties.externalizable.images.url}tinybutton-reffromacctlines.gif" alt="refresh from account lines" title="refresh from account lines" styleClass="tinybutton"/>
      </div>
    </c:if>
    <h3>1099 Classification</h3>
    <table class="datatable" style="border-collapse:collapse;" summary="1099 income types section">
      <tr>
        <kul:htmlAttributeHeaderCell literalLabel="Seq#"/>
        <kul:htmlAttributeHeaderCell attributeEntry="${incomeTypeAttributes.chartOfAccountsCode}"/>
        <kul:htmlAttributeHeaderCell attributeEntry="${incomeTypeAttributes.incomeTypeCode}"/>
        <kul:htmlAttributeHeaderCell attributeEntry="${incomeTypeAttributes.amount}"/>
        <c:if test="${canEditIncomeType}">
          <kul:htmlAttributeHeaderCell literalLabel="Actions"/>
        </c:if>
      </tr>
      <c:if test="${canEditIncomeType}">
        <tr>
          <kul:htmlAttributeHeaderCell literalLabel="add:" scope="row" />
          <td class="infoline"><kul:htmlControlAttribute attributeEntry="${incomeTypeAttributes.chartOfAccountsCode}" property="document.incomeTypeHandler.newIncomeType.chartOfAccountsCode" /></td>
          <td class="infoline"><kul:htmlControlAttribute attributeEntry="${incomeTypeAttributes.incomeTypeCode}" property="document.incomeTypeHandler.newIncomeType.incomeTypeCode" /></td>
          <td class="infoline" style="text-align: right"><kul:htmlControlAttribute attributeEntry="${incomeTypeAttributes.amount}" property="document.incomeTypeHandler.newIncomeType.amount" styleClass="amount"/></td>
          <td class="infoline">
            <div align="center">
              <html:image property="methodToCall.newIncomeType" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" alt="Add Income Type" title="Add Income Type" styleClass="tinybutton"/>
            </div>
          </td>
        </tr>
      </c:if>
      <logic:iterate id="incomeType" name="KualiForm" property="document.incomeTypes" indexId="ctr">
        <tr>
          <kul:htmlAttributeHeaderCell literalLabel="${ctr+1}" scope="row">
            <html:hidden property="document.incomeTypes[${ctr}].lineNumber" />
            <html:hidden property="document.incomeTypes[${ctr}].versionNumber" />
          </kul:htmlAttributeHeaderCell>
          <td class="datacell">
            <kul:htmlControlAttribute attributeEntry="${incomeTypeAttributes.chartOfAccountsCode}" property="document.incomeTypes[${ctr}].chartOfAccountsCode" readOnly="true"/>
          </td>
          <td class="datacell">
          
            <kul:htmlControlAttribute attributeEntry="${incomeTypeAttributes.incomeTypeCode}" property="document.incomeTypes[${ctr}].incomeType.incomeTypeCode" readOnly="true"/>
          </td>
          <td class="datacell" style="text-align: right">
            <kul:htmlControlAttribute attributeEntry="${incomeTypeAttributes.amount}" property="document.incomeTypes[${ctr}].amount" readOnly="${not canEditIncomeType}" styleClass="amount"/>
          </td>
          <c:if test="${canEditIncomeType}">
            <td class="datacell">
              <div align="center">
              </div>
            </td>
          </c:if>
        </tr>
      </logic:iterate>
      <tr>
        <td colspan="4" class="total-line" style="font-weight:bold;">Total: ${KualiForm.document.incomeTypeHandler.totalIncomeTypeAmount}</td>
        <td class="total-line">&nbsp;</td>
      </tr>
    </table>
  </div>
</kul:tab>
