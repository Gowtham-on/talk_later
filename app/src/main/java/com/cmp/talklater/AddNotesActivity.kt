package com.cmp.talklater

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.app.NotificationManagerCompat
import com.cmp.talklater.ui.components.AddNotesBottomSheet
import com.cmp.talklater.ui.theme.TalkLaterTheme
import com.cmp.talklater.util.ThemeUtil
import com.cmp.talklater.worker.scheduler.scheduleReminderNotification
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddNotesActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val number = intent.getStringExtra("number")
        val name = intent.getStringExtra("name")
        val id = intent.getIntExtra("notificationId", 100)

        // Dismiss notification
        NotificationManagerCompat.from(this).cancel(id)
        val prefs = this.getSharedPreferences("theme_pref", MODE_PRIVATE)
        var theme = ThemeUtil.valueOf(prefs.getString("theme", ThemeUtil.SYSTEM.name).toString())

        setContent {
            var showSheet by remember { mutableStateOf(true) }
            var selectedOption by remember { mutableStateOf<Pair<Int, String>?>(null) }

            TalkLaterTheme(theme = theme) {
                AddNotesBottomSheet(
                    showSheet = showSheet,
                    onDismiss = {
                        showSheet = false
                        finish()
                    },
                    onOptionSelected = { option ->
                        selectedOption = option
                        val displayName = if (name.isNullOrBlank()) number else name
                        scheduleReminderNotification(
                            context = this,
                            delayInMinutes = option.first,
                            title = "Call Reminder",
                            message = "Don’t forget to call back $displayName!",
                            notificationId = id,
                            number = number ?: "",
                        )
                        Toast.makeText(
                            this,
                            "You will be reminded in ${option.second}",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                )
            }
        }
    }
}