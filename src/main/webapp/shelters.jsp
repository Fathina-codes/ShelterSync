<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:include page="header.jsp"><jsp:param name="active" value="shelters"/></jsp:include>


<h2>Shelter Management</h2>
<p class="subtitle">Add shelters and monitor how many beds are still free.</p>

<c:if test="${not empty error}">
  <div class="alert alert-error">${error}</div>
</c:if>

<div class="card">
  <h3>${empty edit ? 'Add New Shelter' : 'Edit Shelter'}</h3>
  <form method="post" action="shelters">
    <input type="hidden" name="id" value="${edit.id}">
    <div class="form-row">
      <div class="field">
        <label>Shelter name</label>
        <input name="name" placeholder="e.g. Govt School Shelter" value="${edit.name}" required>
      </div>
      <div class="field">
        <label>Location</label>
        <input name="location" placeholder="e.g. Velachery" value="${edit.location}" required>
      </div>
      <div class="field">
        <label>Capacity</label>
        <input name="capacity" type="number" min="1" placeholder="e.g. 200" value="${edit.capacity}" required>
      </div>
      <button class="btn" type="submit">${empty edit ? 'Add Shelter' : 'Update'}</button>
      <c:if test="${not empty edit}">
        <a class="btn btn-gray" href="shelters">Cancel</a>
      </c:if>
    </div>
  </form>
</div>

<div class="card">
  <h3>All Shelters</h3>
  <table>
    <tr>
      <th>ID</th><th>Name</th><th>Location</th><th>Capacity</th>
      <th>Occupancy</th><th>Available</th><th>Status</th><th>Actions</th>
    </tr>
    <c:forEach var="s" items="${shelters}">
      <fmt:formatNumber var="pct" value="${s.occupancy * 100 / s.capacity}" maxFractionDigits="0" groupingUsed="false" />
      <tr>
        <td>${s.id}</td>
        <td><strong>${s.name}</strong></td>
        <td>${s.location}</td>
        <td>${s.capacity}</td>
        <td>
          <div class="bar ${pct >= 100 ? 'full' : (pct >= 75 ? 'warn' : '')}">
            <div data-pct="${pct}"></div>
          </div>
          <small>${s.occupancy} / ${s.capacity}</small>
        </td>
        <td>${s.available}</td>
        <td>
          <c:choose>
            <c:when test="${s.available <= 0}"><span class="badge badge-full">FULL</span></c:when>
            <c:otherwise><span class="badge badge-open">OPEN</span></c:otherwise>
          </c:choose>
        </td>
        <td>
          <a class="btn btn-sm btn-edit" href="shelters?action=edit&id=${s.id}">Edit</a>
          <a class="btn btn-sm btn-delete" href="shelters?action=delete&id=${s.id}"
             onclick="return confirm('Delete this shelter?')">Delete</a>
        </td>
      </tr>
    </c:forEach>
    <c:if test="${empty shelters}">
      <tr><td colspan="8" class="empty">No shelters yet. Add one above.</td></tr>
    </c:if>
  </table>
</div>
<script>
  document.querySelectorAll('.bar > div[data-pct]').forEach(function (d) {
    d.style.width = d.dataset.pct + '%';
  });
</script>

<jsp:include page="footer.jsp"/>