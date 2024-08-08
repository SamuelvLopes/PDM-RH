// Path: app/src/main/java/com/incompanyapp/data/model/PunchClock.kt

package com.incompanyapp.data.model

data class PunchClock(
    val id: Int,
    val employeeId: Int,
    val timestamp: Long,
    val type: PunchType
)

enum class PunchType {
    CHECK_IN, CHECK_OUT
}
