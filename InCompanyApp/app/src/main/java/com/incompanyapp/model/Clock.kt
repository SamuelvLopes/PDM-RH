package com.incompanyapp.model

import java.time.LocalDate
import java.time.LocalTime

data class Clock(
    val date: LocalDate,
    val clockIn: LocalTime,
    val clockOut: LocalTime? = null
)
