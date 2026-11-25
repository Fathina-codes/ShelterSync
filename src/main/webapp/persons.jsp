<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="header.jsp"><jsp:param name="active" value="persons"/></jsp:include>

<h2>Affected People</h2>
<p class="subtitle">Register people into a shelter. Occupancy updates automatically.</p>

<c:if test="${not empty error}"><div class="alert alert-error">${error}</div></c:if>

<div class="card">
  <h3>Register Person</h3>
  <form method="post" action="persons">
    <div class="form-row">
      <div class="field"><label>Name</label><input name="name" required></div>
      <div class="field"><label>Age</label><input name="age" type="number" min="0" max="120" required></div>
      <div class="field"><label>Gender</label>
        <select name="gender"><option>Male</option><option>Female</option><option>Other</option></select>
      </div>
      <div class="field"><label>Contact</label><input name="contact" maxlength="15"></div>
      <div class="field"><label>Shelter</label>
        <select name="shelter_id" required>
          <c:forEach var="s" items="${shelters}">
            <option value="${s.shelter_id}" ${s.occupancy >= s.capacity ? 'disabled' : ''}>
              ${s.name} (${s.capacity - s.occupancy} beds free)
            </option>
          </c:forEach>
        </select>
      </div>
      <button class="btn" type="submit">Register</button>
    </div>
  </form>
</div>

<div class="card">
  <h3>Registered People</h3>
  <table>
    <tr><th>ID</th><th>Name</th><th>Age</th><th>Gender</th><th>Contact</th><th>Shelter</th><th>Action</th></tr>
    <c:forEach var="p" items="${persons}">
      <tr>
        <td>${p.person_id}</td><td><strong>${p.name}</strong></td><td>${p.age}</td>
        <td>${p.gender}</td><td>${p.contact}</td><td>${p.shelter}</td>
        <td><a class="btn btn-sm btn-delete" href="persons?action=delete&id=${p.person_id}"
               onclick="return confirm('Remove this person?')">Remove</a></td>
      </tr>
    </c:forEach>
    <c:if test="${empty persons}"><tr><td colspan="7" class="empty">No one registered yet.</td></tr></c:if>
  </table>
</div>

<jsp:include page="footer.jsp"/>