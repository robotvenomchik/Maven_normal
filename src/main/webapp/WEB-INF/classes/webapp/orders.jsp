<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*, com.example.Order, com.example.Product" %>
<%@ page import="jakarta.persistence.*" %>
<html>
<head>
    <title>Orders List</title>
</head>
<body>
    <h2>Order List</h2>

    <form action="orders" method="get">
        <label for="orderId">Enter Order ID:</label>
        <input type="text" id="orderId" name="id">
        <button type="submit">Get Order</button>
    </form>

    <%
        String orderId = request.getParameter("id");
        if (orderId != null) {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("example-unit");
            EntityManager em = emf.createEntityManager();
            try {
                Order order = em.find(Order.class, Long.parseLong(orderId));
                if (order != null) {
    %>
                    <h3>Order Details:</h3>
                    <p><strong>ID:</strong> <%= order.getId() %></p>
                    <p><strong>Date:</strong> <%= order.getDate() %></p>
                    <p><strong>Cost:</strong> <%= order.getCost() %></p>
                    <h4>Products:</h4>
                    <ul>
                        <%
                            for (Product product : order.getProducts()) {
                        %>
                            <li><%= product.getName() %> - $<%= product.getCost() %></li>
                        <%
                            }
                        %>
                    </ul>
    <%
                } else {
    %>
                    <p>Order not found.</p>
    <%
                }
            } finally {
                em.close();
                emf.close();
            }
        }
    %>
</body>
</html>
