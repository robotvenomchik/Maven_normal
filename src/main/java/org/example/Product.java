package org.example;

import jakarta.persistence.*;

@Entity
@Table(name = "products")
class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Double cost;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false) // Додаємо зовнішній ключ
    private Order order;

    public void setOrder(Order order) {
        this.order = order;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }
}
