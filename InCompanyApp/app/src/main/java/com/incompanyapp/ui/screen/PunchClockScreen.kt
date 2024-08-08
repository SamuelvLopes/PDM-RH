package com.incompanyapp.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.incompanyapp.data.model.PunchType
import com.incompanyapp.data.repository.punchClock

@Composable
fun PunchClockScreen(employeeId: Int) {
    Column {
        Button(onClick = { punchClock(employeeId, PunchType.CHECK_IN) }) {
            Text("Check In")
        }
        Button(onClick = { punchClock(employeeId, PunchType.CHECK_OUT) }) {
            Text("Check Out")
        }
    }
}

@Preview
@Composable
fun PreviewPunchClockScreen() {
    PunchClockScreen(employeeId = 1)
}
