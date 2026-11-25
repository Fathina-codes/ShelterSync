<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="header.jsp"><jsp:param name="active" value="resources"/></jsp:include>

<h2>Resources &amp; Inventory</h2>
<p class="subtitle">Food, water, medicine and clothing. Items below 50 units are marked LOW.</p>

<c:if test="${not empty error}"><div class="alert alert-error">${error}</div></c:if>

<div class="card">
  <h3>Add New Resource</h3>
  <form method="post" action="resources">
    <div class="form-row">
      <div class="field"><label>Name</label><input name="name" placeholder="e.g. Rice Bags" required></div>
      <div class="field"><label>Category</label>
        <select name="category"><option>Food</option><option>Water</option><option>Medicine</option><option>Clothing</option></select>
      </div>
      <div class="field"><label>Opening quantity</label><input name="quantity" type="number" min="0" value="0" required></div>
      <button class="btn" type="submit">Add</button>
    </div>
  </form>
</div>

<div class="card">
  <h3>Current Stock</h3>
  <table>
    <tr><th>ID</th><th>Name</th><th>Category</th><th>Quantity</th><th>Level</th><th>Add stock</th><th>Action</th></tr>
    <c:forEach var="r" items="${resources}">
      <tr>
        <td>${r.resource_id}</td><td><strong>${r.name}</strong></td><td>${r.category}</td><td>${r.quantity}</td>
        <td><span class="badge ${r.quantity < 50 ? 'badge-full' : 'badge-open'}">${r.quantity < 50 ? 'LOW' : 'OK'}</span></td>
        <td>
          <form method="post" action="resources" style="display:flex;gap:6px">
            <input type="hidden" name="resource_id" value="${r.resource_id}">
            <input name="add_qty" type="number" min="1" value="10" style="width:70px;padding:5px">
            <button class="btn btn-sm" type="submit">+ Add</button>
          </form>
        </td>
        <td><a class="btn btn-sm btn-delete" href="resources?action=delete&id=${r.resource_id}"
               onclick="return confirm('Delete this resource?')">Delete</a></td>
      </tr>
    </c:forEach>
    <c:if test="${empty resources}"><tr><td colspan="7" class="empty">No resources yet.</td></tr></c:if>
  </table>
</div>

<jsp:include page="footer.jsp"/>