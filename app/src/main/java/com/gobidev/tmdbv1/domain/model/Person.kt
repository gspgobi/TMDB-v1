package com.gobidev.tmdbv1.domain.model

data class Person(
    val id: Int,
    val name: String,
    val profileUrl: String?,
    val knownForDepartment: String
)
