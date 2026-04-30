package org.acme;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

@Entity
public class Coffee extends PanacheEntity {
    public String name;
    public String countryOfOrigin;
    public Integer price;
}
