<%@ taglib prefix="c" uri="/tlds/c.tld" %>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/tlds/fmt.tld" prefix="fmt" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="kul" %>
<%@ taglib tagdir="/WEB-INF/tags/dd" prefix="dd" %>
<%@ taglib tagdir="/WEB-INF/tags/kra" prefix="kra" %>
<%@ taglib tagdir="/WEB-INF/tags/kra/budget" prefix="kra-b" %>

<c:set var="budgetPeriodAttributes" value="${DataDictionary.BudgetPeriod.attributes}" /> <c:set var="KraConstants" value="${KraConstants}" />
<c:set var="businessObjectClass" value="${DataDictionary.BudgetPeriod.businessObjectClass}" />
<c:set var="viewOnly" value="${KualiForm.editingMode['viewOnly']}"/>

        <kul:tab tabTitle="Budget Periods" defaultOpen="true" tabErrorKey="document.budget.period*,newPeriod**">
              <div class="tab-container" id="G02" style="" align="center">
        

<div class="h2-container"> <span class="subhead-left">
  <h2><a name="Periods"></a>Budget Periods</h2>
  </span><span class="subhead-right"><kul:help businessObjectClassName="${businessObjectClass}" altText="help"/></span> </div>
<table cellpadding="0" cellspacing="0" class="datatable" summary=""> <tbody>
  <tr>
    <th width="2%"  scope="row">&nbsp;</th>
    <th ><div align="left">* Date Range</div></th>
    <th  width="10%"><div align="center">Number of Participants/Trainees</div></th>
    <c:if test="${not viewOnly}"><th  width="2%">Action</th></c:if>
  </tr>
  <!-- Add new line option is displayed
  			only if the number of periods is less than the maximum. -->
  <c:if test="${KualiForm.document.periodListSize lt KraConstants.maximumNumberOfPeriods && not viewOnly}">
  <tr>
    <th width="2%"  scope="row"><div align="right">add:</div></th>
    <td class="infoline"><html:hidden property="newPeriod.documentHeaderId" /> <html:hidden property="newPeriod.budgetPeriodSequenceNumber" /> <html:hidden property="newPeriod.objectId" /> <html:hidden property="newPeriod.versionNumber"/> from: <kul:htmlControlAttribute property="newPeriod.budgetPeriodBeginDate" attributeEntry="${budgetPeriodAttributes.budgetPeriodBeginDate}" readOnly="${viewOnly}" datePicker="true"/> &nbsp;&nbsp;&nbsp;to: <kul:htmlControlAttribute property="newPeriod.budgetPeriodEndDate" attributeEntry="${budgetPeriodAttributes.budgetPeriodEndDate}" datePicker="true"/> </td>
    <td class="infoline"><div align="center"><kul:htmlControlAttribute property="newPeriod.budgetPeriodParticipantsNumber" attributeEntry="${budgetPeriodAttributes.budgetPeriodParticipantsNumber}" /></div></td>
    <td class="infoline"><div align="center"> <html:image property="methodToCall.insertPeriodLine.anchor${currentTabIndex}" styleClass="tinybutton" src="images/tinybutton-add1.gif" alt="add period line"/> </div></td>
  </tr>
  </c:if>
  <!-- Show all currently attached tasks.  -->
  <logic:iterate id="periodLine" name="KualiForm" property="document.budget.periods" indexId="ctr">
  <tr>
    <th width="2%"  scope="row"><div align="right">${ctr+1}:</div></th>
    <td><html:hidden property="document.budget.period[${ctr}].documentHeaderId" /> <html:hidden property="document.budget.period[${ctr}].budgetPeriodSequenceNumber" /> <html:hidden property="document.budget.period[${ctr}].objectId"/> <html:hidden property="document.budget.period[${ctr}].versionNumber" /> from: <kul:htmlControlAttribute property="document.budget.period[${ctr}].budgetPeriodBeginDate" attributeEntry="${budgetPeriodAttributes.budgetPeriodBeginDate}" readOnly="${viewOnly}" datePicker="true"/> &nbsp;&nbsp;&nbsp;to: <kul:htmlControlAttribute property="document.budget.period[${ctr}].budgetPeriodEndDate" attributeEntry="${budgetPeriodAttributes.budgetPeriodEndDate}" readOnly="${viewOnly}" datePicker="true"/> </td>
    <td><div align="center"><kul:htmlControlAttribute property="document.budget.period[${ctr}].budgetPeriodParticipantsNumber" attributeEntry="${budgetPeriodAttributes.budgetPeriodParticipantsNumber}" readOnly="${viewOnly}" /></div></td>
    <c:if test="${not viewOnly}"><td><div align="center"> <c:choose><c:when test="${ctr eq KualiForm.document.periodListSize-1}"> <html:image property="methodToCall.deletePeriodLine.anchorPeriods.line${ctr}" styleClass="tinybutton" src="images/tinybutton-delete1.gif" alt="delete"/> </c:when><c:otherwise>&nbsp;</c:otherwise></c:choose> </div></td></c:if>
  </tr>
  </logic:iterate>
  <!-- End periods list. -->
</tbody></table>
<div align="right">*required&nbsp;&nbsp;&nbsp;</div>
</div>
</kul:tab>