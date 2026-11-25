<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:if test="${empty stats}"><c:redirect url="dashboard"/></c:if>
<jsp:include page="header.jsp"><jsp:param name="active" value="home"/></jsp:include>

<div class="hero">
  <h1>ShelterSync Dashboard</h1>
  <p>Live overview of shelters, people, requests and supplies.</p>
</div>

<div class="stats">
  <div class="stat"><div class="num">${stats.shelters}</div><div class="lbl">Shelters</div></div>
  <div class="stat"><div class="num">${stats.people}</div><div class="lbl">People registered</div></div>
  <div class="stat"><div class="num">${stats.occupied} / ${stats.capacity}</div><div class="lbl">Beds occupied</div></div>
  <div class="stat ${stats.pending > 0 ? 'danger' : ''}"><div class="num">${stats.pending}</div><div class="lbl">Pending requests</div></div>
  <div class="stat ${stats.critical > 0 ? 'danger' : ''}"><div class="num">${stats.critical}</div><div class="lbl">Critical requests</div></div>
  <div class="stat ${stats.lowstock > 0 ? 'danger' : ''}"><div class="num">${stats.lowstock}</div><div class="lbl">Low-stock items</div></div>
</div>

<div class="card">
  <h3>Shelter Occupancy</h3>
  <table>
    <tr><th>ID</th><th>Shelter</th><th>Occupancy</th><th>Registered people</th></tr>
    <c:forEach var="s" items="${shelterList}">
      <tr>
        <td>${s.shelter_id}</td><td><strong>${s.name}</strong></td>
        <td>
          <div class="bar ${s.pct >= 100 ? 'full' : (s.pct >= 75 ? 'warn' : '')}"><div data-pct="${s.pct}"></div></div>
          <small>${s.occupancy} / ${s.capacity} (${s.pct}%)</small>
        </td>
        <td>${s.registered}</td>
      </tr>
    </c:forEach>
  </table>
</div>

<div class="card">
  <h3>Top Pending Requests</h3>
  <table>
    <tr><th>ID</th><th>Shelter</th><th>Resource</th><th>Qty</th><th>Urgency</th></tr>
    <c:forEach var="r" items="${topRequests}">
      <tr>
        <td>${r.request_id}</td><td>${r.shelter}</td><td>${r.resource}</td><td>${r.quantity_needed}</td>
        <td><span class="badge badge-${r.urgency.toLowerCase()}">${r.urgency}</span></td>
      </tr>
    </c:forEach>
    <c:if test="${empty topRequests}"><tr><td colspan="5" class="empty">No pending requests.</td></tr></c:if>
  </table>
</div>

<div class="card">
  <h3>Supplies Distributed by Category</h3>
  <table>
    <tr><th>Category</th><th>Total units distributed</th></tr>
    <c:forEach var="c" items="${byCategory}"><tr><td>${c.category}</td><td>${c.total}</td></tr></c:forEach>
  </table>
</div>

<script>
  document.querySelectorAll('.bar > div[data-pct]').forEach(function (d) {
    d.style.width = d.dataset.pct + '%';
  });
</script>

<jsp:include page="footer.jsp"/>