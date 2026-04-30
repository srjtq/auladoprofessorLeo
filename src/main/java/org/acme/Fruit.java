package org.acme;

import io.quarkus.hibernate.orm.panache.Panache;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.smallrye.common.constraint.NotNull;
import jakarta.persistence.Entity;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Entity
@Schema(description = "Uma fruta")
public class Fruit extends PanacheEntity {
    @NotNull
    @Schema(description = "Nome da fruta", examples = {"Maça", "Banana"}, maxLength = 100
    , required = true)
    public String name;
    @Schema(description = "Descrição da fruta", examples = {"Fruta de inverno"}
    , maxLength = 255)
    public String description;
}
