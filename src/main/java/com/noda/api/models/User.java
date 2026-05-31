package com.noda.api.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "tb_user")
@NoArgsConstructor
@AllArgsConstructor
public class  User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "CPF cannot be blank")
    @Column(unique = true, length = 11)
    private String cpf;

    @NotBlank(message = "Name cannot be blank")
    private String name;

    @NotNull(message = "Birthday cannot be null")
    private LocalDate birthday;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid  email format")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "Password cannot be blank")
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Account> accounts;
}