package com.incompanyapp.data.repository

import com.incompanyapp.data.model.PunchClock
import com.incompanyapp.data.model.PunchType

fun punchClock(employeeId: Int, type: PunchType) {
    val punchClock = PunchClock(id = PunchClockRepository.getPunchClocksForEmployee(employeeId).size + 1, employeeId = employeeId, timestamp = System.currentTimeMillis(), type = type)
    PunchClockRepository.addPunchClock(punchClock)
}
