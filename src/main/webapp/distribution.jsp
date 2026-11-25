<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="header.jsp"><jsp:param name="active" value="distribution"/></jsp:include>

<h2>Distribution</h2>
<p class="subtitle">Send supplies to a shelter. Stock reduces and the matching request is marked fulfilled automatically.</p>

<c:if test="${not empty error}"><div class="alert alert-error">${error}</div></c:if>

<div class="card">
  <h3>Distribute Supplies</h3>
  <form method="post" action="distribution">
    <div class="form-row">
      <div class="field"><label>Pending request (auto-fill)</label>
        <select onchange="fillFromRequest(this)">
          <option value="">-- manual entry --</option>
          <c:forEach var="q" items="${pending}">
            <option value="${q.request_id}" data-shelter="${q.shelter_id}"
                    data-resource="${q.resource_id}" data-qty="${q.quantity_needed}">
              #${q.request_id} ${q.urgency}: ${q.resource} x${q.quantity_needed} for ${q.shelter}
            </option>
          </c:forEach>
        </select>
      </div>
      <div class="field"><label>Shelter</label>
        <select name="shelter_id" id="shelterSel">
          <c:forEach var="s" items="${shelters}"><option value="${s.shelter_id}">${s.name}</option></c:forEach>
        </select>
      </div>
      <div class="field"><label>Resource</label>
        <select name="resource_id" id="resourceSel">
          <c:forEach var="r" items="${resources}"><option value="${r.resource_id}">${r.name} (${r.quantity} in stock)</option></c:forEach>
        </select>
      </div>
      <div class="field"><label>Quantity</label><input name="quantity" id="qtyInput" type="number" min="1" required></div>
      <button class="btn" type="submit">Distribute</button>
    </div>
  </form>
</div>

<div class="card">
  <h3>Distribution History</h3>
  <table>
    <tr><th>ID</th><th>Shelter</th><th>Resource</th><th>Quantity</th><th>Date</th></tr>
    <c:forEach var="d" items="${history}">
      <tr><td>${d.dist_id}</td><td>${d.shelter}</td><td>${d.resource}</td><td>${d.quantity}</td><td>${d.dist_date}</td></tr>
    </c:forEach>
    <c:if test="${empty history}"><tr><td colspan="5" class="empty">Nothing distributed yet.</td></tr></c:if>
  </table>
</div>

<script>
  function fillFromRequest(sel) {
    var o = sel.options[sel.selectedIndex];
    if (!o.value) return;
    document.getElementById('shelterSel').value = o.dataset.shelter;
    document.getElementById('resourceSel').value = o.dataset.resource;
    document.getElementById('qtyInput').value = o.dataset.qty;
  }
</script>

<jsp:include page="footer.jsp"/>