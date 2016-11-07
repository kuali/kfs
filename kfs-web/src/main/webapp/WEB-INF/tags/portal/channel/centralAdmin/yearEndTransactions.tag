<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<channel:portalChannelTop channelTitle="Year End Transactions" />
<div class="body">
    <c:if test="${ConfigProperties.module.labor.distribution.enabled == 'true'}">
	    <strong>Labor Distribution</strong><br />
	    <ul class="chan">
		  	<li><portal:portalLink displayTitle="true" title="Year End Benefit Expense Transfer" url="${ConfigProperties.application.url}/laborYearEndBenefitExpenseTransfer.do?methodToCall=docHandler&command=initiate&docTypeName=YEBT" /></li>
	    </ul>
    </c:if>
</div>
<channel:portalChannelBottom />
