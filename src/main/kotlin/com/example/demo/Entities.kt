package com.example.demo

import javax.persistence.*

@Entity
@Table(name="user")
class User(
        @Column(name = "userId")
        var userId : String,
        @Column(name = "password")
        var password : String,
        @Id
        @GeneratedValue
        @Column(name = "id")
        var id : Long? = null
)