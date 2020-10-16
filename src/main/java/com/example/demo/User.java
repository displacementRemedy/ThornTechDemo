package com.example.demo;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class User {

    @Id
    @GeneratedValue
    @CsvIgnore
    @Column(unique = true)
    private Long id;

    @CsvBindByName
    @Column(unique = true)
    private String phone;

    @CsvBindByName
    private String firstName;

    @CsvBindByName
    private String lastName;

}
