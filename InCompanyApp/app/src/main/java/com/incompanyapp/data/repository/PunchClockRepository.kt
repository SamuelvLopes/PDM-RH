package com.incompanyapp.data.repository

import com.incompanyapp.data.model.PunchClock

object PunchClockRepository {
    private val punchClocks = mutableListOf<PunchClock>()

    fun addPunchClock(punchClock: PunchClock) {
        punchClocks.add(punchClock)
    }

    fun getPunchClocksForEmployee(employeeId: Int): List<PunchClock> {
        return punchClocks.filter { it.employeeId == employeeId }
    }
}
