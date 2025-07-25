# TalkLater

TalkLater is a sample Android app written in Kotlin/Jetpack Compose. It keeps track of missed and rejected calls and reminds you to return them later.

The app no longer requires call log permissions. Instead, it listens for the default dialer's missed‑call notifications using a `NotificationListenerService`. When a missed call is detected, TalkLater creates an entry in its database and posts a reminder notification with options to call back or snooze.

Other features include:

- Reminder notifications with custom snooze times
- Automatic deletion of saved logs on a schedule
- Theme selection and basic settings screen
- Firebase hosted privacy policy

To use the app, you must grant it notification access and allow it to post notifications. You can enable notification access from the permission screen when launching the app.
