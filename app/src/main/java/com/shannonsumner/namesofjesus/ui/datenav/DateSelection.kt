package com.shannonsumner.namesofjesus.ui.datenav

import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

internal fun dateLabel(date: LocalDate, locale: Locale = Locale.getDefault()): String =
    "${date.month.getDisplayName(TextStyle.FULL, locale)} ${date.dayOfMonth}"
