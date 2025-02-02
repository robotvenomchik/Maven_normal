package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/orders")
public class OrderServlet extends HttpServlet {
    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("example-unit");
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("Received POST request to /orders");
        resp.getWriter().write("POST request received");
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            Order order = objectMapper.readValue(req.getInputStream(), Order.class);

            transaction.begin();
            em.persist(order);
            transaction.commit();

            resp.setStatus(HttpServletResponse.SC_CREATED);
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace(); // Додайте логування для дебагу
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            em.close();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id");

        if (idParam == null || idParam.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"Missing or empty 'id' parameter\"}");
            return;
        }

        try {
            Long orderId = Long.valueOf(idParam);
            EntityManager em = emf.createEntityManager();
            try {
                Order order = em.find(Order.class, orderId);
                if (order != null) {
                    resp.setContentType("application/json");
                    objectMapper.writeValue(resp.getOutputStream(), order);
                    resp.setStatus(HttpServletResponse.SC_OK);
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write("{\"error\": \"Order not found\"}");
                }
            } finally {
                em.close();
            }
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"Invalid 'id' format\"}");
        }
    }


    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long orderId = Long.valueOf(req.getParameter("id"));
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            Order updatedOrder = objectMapper.readValue(req.getInputStream(), Order.class);
            transaction.begin();
            Order existingOrder = em.find(Order.class, orderId);
            if (existingOrder != null) {
                existingOrder.setDate(updatedOrder.getDate());
                existingOrder.setCost(updatedOrder.getCost());
                existingOrder.setProducts(updatedOrder.getProducts());
                transaction.commit();
                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                transaction.rollback();
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            transaction.rollback();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            em.close();
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long orderId = Long.valueOf(req.getParameter("id"));
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            Order existingOrder = em.find(Order.class, orderId);
            if (existingOrder != null) {
                em.remove(existingOrder);
                transaction.commit();
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                transaction.rollback();
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            transaction.rollback();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            em.close();
        }
    }
}
