package com.shannonsumner.namesofjesus.ui.datenav

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.util.Locale

class DateSelectionTest {
    @Test
    fun dateLabelOmitsYear() {
        assertEquals("February 29", dateLabel(LocalDate.of(2024, 2, 29), Locale.US))
    }
}
