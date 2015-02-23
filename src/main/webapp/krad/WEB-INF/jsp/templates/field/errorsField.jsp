<%--

    Copyright 2005-2014 The Kuali Foundation

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
<%@ include file="/krad/WEB-INF/jsp/tldHeader.jsp" %>

<tiles:useAttribute name="field" classname="org.kuali.rice.krad.uif.field.ErrorsField"/>

<krad:div component="${field}">
  <!-- Error messages - div generated but hidden if no server errors, for client side error placement-->
  <c:choose>
    <c:when test="${field.errorCount > 0 && field.displayErrorMessages}">
      <div id="${field.id}_errorMessages" class="kr-errorMessages" aria-live="assertive" aria-relevant="additions removals" aria-atomic="true">
    </c:when>
    <c:otherwise>
      <div id="${field.id}_errorMessages" style="display: none;" class="kr-errorMessages" aria-live="assertive" aria-relevant="additions removals">
    </c:otherwise>
  </c:choose>

  <c:if test="${field.displayErrorTitle}">
    <strong>${field.errorTitle}</strong>
  </c:if>

  <c:if test="${field.displayCounts && field.errorCount > 0}">
    <span>${field.errorCount} errors</span>
  </c:if>

  <ul class="errorLines">
    <c:if test="${field.displayErrorMessages}">
      <c:forEach var="message" items="${field.errors}">
        <li>${message}</li>
      </c:forEach>
    </c:if>
  </ul>
  </div>

  <!-- Warning messages -->
  <c:if test="${field.warningCount > 0 && field.displayWarningMessages}">
    <div id="${field.id}_warningMessages" class="kr-warningMessages">
      <c:if test="${field.displayWarningTitle}">
        <strong>${field.warningTitle}</strong>
      </c:if>

      <c:if test="${field.displayCounts && field.warningCount > 0}">
        <span>${field.warningCount} warnings</span>
      </c:if>

      <ul aria-live="assertive" aria-relevant="additions removals">
        <c:if test="${field.displayWarningMessages}">
          <c:forEach var="message" items="${field.warnings}">
            <li>${message}</li>
          </c:forEach>
        </c:if>
      </ul>
    </div>
  </c:if>

  <!-- Info messages -->
  <c:if test="${field.infoCount > 0 && field.displayInfoMessages}">
    <div id="${field.id}_infoMessages" class="kr-infoMessages">
      <c:if test="${field.displayInfoTitle}">
        <strong>${field.infoTitle}</strong>
      </c:if>

      <c:if test="${field.displayCounts && field.infoCount > 0}">
        <span>${field.infoCount} informational messages</span>
      </c:if>

      <ul aria-live="assertive" aria-relevant="additions removals">
        <c:if test="${field.displayInfoMessages}">
          <c:forEach var="message" items="${field.infos}">
            <li>${message}</li>
          </c:forEach>
        </c:if>
      </ul>
    </div>
  </c:if>
  <krad:script value="
    ${field.growlScript}
    clearServerErrorColors('${field.id}_div');
  "/>

  <c:if test="${(field.infoCount > 0 || field.warningCount > 0 || field.errorCount > 0)}">
    <krad:script value="
        applyErrorColors('${field.id}_div', ${field.errorCount}, ${field.warningCount}, ${field.infoCount}, false);
        showFieldIcon('${field.id}_div', ${field.errorCount});
      "/>
  </c:if>
</krad:div>

