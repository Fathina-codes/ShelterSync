<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="header.jsp"><jsp:param name="active" value="requests"/></jsp:include>

<h2>Emergency Requests</h2>
<p class="subtitle">Critical requests always appear at the top. Fulfilled ones move to the bottom.</p>

<div class="card">
  <h3>Raise a Request</h3>
  <form method="post" action="requests">
    <div class="form-row">
      <div class="field"><label>Shelter</label>
        <select name="shelter_id">
          <c:forEach var="s" items="${shelters}"><option value="${s.shelter_id}">${s.name}</option></c:forEach>
        </select>
      </div>
      <div class="field"><label>Resource needed</label>
        <select name="resource_id">
          <c:forEach var="r" items="${resources}"><option value="${r.resource_id}">${r.name}</option></c:forEach>
        </select>
      </div>
      <div class="field"><label>Quantity</label><input name="quantity_needed" type="number" min="1" required></div>
      <div class="field"><label>Urgency</label>
        <select name="urgency"><option>CRITICAL</option><option>HIGH</option><option selected>MEDIUM</option><option>LOW</option></select>
      </div>
      <button class="btn" type="submit">Submit</button>
    </div>
  </form>
</div>

<div class="card">
  <h3>Request Queue</h3>
  <table>
    <tr><th>ID</th><th>Shelter</th><th>Resource</th><th>Qty</th><th>Urgency</th><th>Status</th><th>Actions</th></tr>
    <c:forEach var="r" items="${requests}">
      <tr>
        <td>${r.request_id}</td><td>${r.shelter}</td><td>${r.resource}</td><td>${r.quantity_needed}</td>
        <td><span class="badge badge-${r.urgency.toLowerCase()}">${r.urgency}</span></td>
        <td><span class="badge ${r.status == 'FULFILLED' ? 'badge-done' : 'badge-pending'}">${r.status}</span></td>
        <td>
          <c:if test="${r.status != 'FULFILLED'}">
            <a class="btn btn-sm" href="requests?action=fulfil&id=${r.request_id}">Mark fulfilled</a>
          </c:if>
          <a class="btn btn-sm btn-delete" href="requests?action=delete&id=${r.request_id}"
             onclick="return confirm('Delete this request?')">Delete</a>
        </td>
      </tr>
    </c:forEach>
    <c:if test="${empty requests}"><tr><td colspan="7" class="empty">No requests yet.</td></tr></c:if>
  </table>
</div>

<jsp:include page="footer.jsp"/>