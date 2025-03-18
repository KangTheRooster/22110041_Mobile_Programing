package com.example.simple_alarm

import android.app.AlarmManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.AlarmClock
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.*
import android.provider.Settings
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkAndRequestAlarmPermission(this)
        setContent {
            AlarmApp(context = this)
        }
    }
}

fun checkAndRequestAlarmPermission(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // Android 12+
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (!alarmManager.canScheduleExactAlarms()) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            context.startActivity(intent)
        }
    }
}

@Composable
fun RollingBarAlarmPicker(
    initialHour: Int,
    initialMinute: Int,
    onTimeSelected: (Int, Int) -> Unit
) {
    val hours = (0..23).toList()
    val minutes = (0..59).toList()

    val hourListState = remember { LazyListState(initialHour) }
    val minuteListState = remember { LazyListState(initialMinute) }

    var selectedHour by remember { mutableStateOf(initialHour) }
    var selectedMinute by remember { mutableStateOf(initialMinute) }

    val coroutineScope = rememberCoroutineScope()
    val listHeight = 150.dp

    fun roundToNearestItem(listState: LazyListState, setValue: (Int) -> Unit) {
        coroutineScope.launch {
            val currentIndex = listState.firstVisibleItemIndex
            val targetIndex = if (listState.firstVisibleItemScrollOffset > 30) currentIndex + 1 else currentIndex
            val newIndex = targetIndex.coerceIn(0, listState.layoutInfo.totalItemsCount - 1)

            if (newIndex != currentIndex) {
                listState.animateScrollToItem(newIndex)
                setValue(newIndex)
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Select Hour and Minute", style = MaterialTheme.typography.headlineSmall)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Box(modifier = Modifier.weight(1f).height(listHeight)) {
                LazyColumn(
                    state = hourListState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = listHeight / 3)
                ) {
                    items(hours) { hour ->
                        Text(
                            text = hour.toString().padStart(2, '0'),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            style = if (hour == selectedHour)
                                MaterialTheme.typography.headlineSmall.copy(color = MaterialTheme.colorScheme.primary)
                            else
                                MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth()
                        .height(40.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                )
            }

            Text(text = ":", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(horizontal = 8.dp))

            Box(modifier = Modifier.weight(1f).height(listHeight)) {
                LazyColumn(
                    state = minuteListState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = listHeight / 3)
                ) {
                    items(minutes) { minute ->
                        Text(
                            text = minute.toString().padStart(2, '0'),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            style = if (minute == selectedMinute)
                                MaterialTheme.typography.headlineSmall.copy(color = MaterialTheme.colorScheme.primary)
                            else
                                MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth()
                        .height(40.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                onTimeSelected(selectedHour, selectedMinute)
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .padding(8.dp)
                .height(48.dp)
        ) {
            Text(text = "Set Time", color = Color.White)
        }
    }

    // Snap to nearest item when scrolling stops
    LaunchedEffect(hourListState.isScrollInProgress) {
        if (!hourListState.isScrollInProgress) {
            roundToNearestItem(hourListState) { selectedHour = it }
        }
    }

    LaunchedEffect(minuteListState.isScrollInProgress) {
        if (!minuteListState.isScrollInProgress) {
            roundToNearestItem(minuteListState) { selectedMinute = it }
        }
    }
}


@Composable
fun CustomButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .padding(8.dp)
            .height(48.dp)
    ) {
        Text(text = text, color = Color.White)
    }
}

@Composable
fun AlarmApp(context: Context) {
    var selectedHour by remember { mutableIntStateOf(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) }
    var selectedMinute by remember { mutableIntStateOf(Calendar.getInstance().get(Calendar.MINUTE)) }
    var label by remember { mutableStateOf("Báo thức") }
    var showTimePicker by remember { mutableStateOf(false) }

    if (showTimePicker) {
        RollingBarAlarmPicker(selectedHour, selectedMinute) { hour, minute ->
            selectedHour = hour
            selectedMinute = minute
            showTimePicker = false
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Alarm", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))

            CustomButton(text = "Set new time") { showTimePicker = true }
            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Alarm set at: $selectedHour:$selectedMinute")
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = label,
                onValueChange = { label = it },
                label = { Text("Name of the alarm") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            CustomButton(text = "Set alarm") { setAlarm(context, selectedHour, selectedMinute, label) } // 🔹 Updated Button
        }
    }
}


fun setAlarm(context: Context, hour: Int, minute: Int, label: String) {
    val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
        putExtra(AlarmClock.EXTRA_HOUR, hour)
        putExtra(AlarmClock.EXTRA_MINUTES, minute)
        putExtra(AlarmClock.EXTRA_MESSAGE, label)
        putExtra(AlarmClock.EXTRA_SKIP_UI, false)
    }
    try {
        context.startActivity(intent)
        Toast.makeText(context, "Your alarm '$label' has been set at $hour:$minute", Toast.LENGTH_SHORT).show()
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, "There is an error please try again", Toast.LENGTH_SHORT).show()
    }
}
