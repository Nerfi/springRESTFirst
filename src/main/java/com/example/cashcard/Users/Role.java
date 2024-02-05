package com.example.cashcard.Users;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
// creo que podria considerar esta clase como un modelo

//public record Role(@Id Long id, String name) {
//}

// nuevo codigo from tutorial

//@Entity
@Table(name = "roles")
public class Role {
    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    //@Enumerated(EnumType.STRING)
   // @Column(length = 20)
    // lo he comentado para seguir el tutorial al pie de la letra
    //private String name;

    private ERole name;

    public Role() {

    }

    public Role(ERole name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ERole getName() {
        return name;
    }

    public void setName(ERole name) {
        this.name = name;
    }
}

// https://www.bezkoder.com/spring-boot-login-example-mysql/
// lo hemos dejado en el parte de crear el Model User
// https://www.sivalabs.in/using-java-records-with-spring-boot-3/