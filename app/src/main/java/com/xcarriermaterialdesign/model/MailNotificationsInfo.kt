package com.xcarriermaterialdesign.model

data class MailNotificationsInfo(
    val NotifyAllStatuses: Boolean,
    val NotifyDelivered: Boolean,
    val NotifyException: Boolean,
    val NotifyMe: Boolean,
    val NotifyOthers: Boolean,
    val NotifyReceived: Boolean,
    val Others_Email: String
)