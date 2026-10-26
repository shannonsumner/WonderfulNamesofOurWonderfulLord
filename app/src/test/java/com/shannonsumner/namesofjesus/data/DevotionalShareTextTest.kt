package com.shannonsumner.namesofjesus.data

import org.junit.Assert.assertEquals
import org.junit.Test

class DevotionalShareTextTest {
    private fun devotional(devo: String, activity: String) = Devotional(
        topic = "The Seed of the Woman",
        scripture = "I will put enmity… between thy seed and her seed.",
        reference = "Genesis 3:15",
        mainScripture = null,
        devo = DevotionalHtml.parseBody(devo),
        activity = DevotionalHtml.parseBody(activity)
    )

    @Test
    fun stripsHtmlAndDecodesEntities() {
        val text = devotional(
            devo = "He came &mdash; <em>the \"Seed\"</em> (<button data-scripture=\"50:2:9\">Philippians 2:9</button>)." +
                "\n\nSecond&hellip; <script>alert(1)</script><b>paragraph</b>&#8217;s end.",
            activity = "<i>Amen.</i>"
        ).toShareText()

        assertEquals(
            "The Seed of the Woman\n\n" +
                "I will put enmity… between thy seed and her seed. (Genesis 3:15)\n\n" +
                "He came — the \"Seed\" (Philippians 2:9).\n\n" +
                "Second… paragraph’s end.\n\n" +
                "Amen.",
            text
        )
    }

    @Test
    fun keepsPoetryLineBreaks() {
        val text = devotional(
            devo = "<em>All through the mountains,\n     And up from the rocky steep,</em>",
            activity = "First line.\n\nSecond line."
        ).toShareText()

        assertEquals(
            "The Seed of the Woman\n\n" +
                "I will put enmity… between thy seed and her seed. (Genesis 3:15)\n\n" +
                "All through the mountains,\n     And up from the rocky steep,\n\n" +
                "First line.\nSecond line.",
            text
        )
    }

    @Test
    fun appendsCopyrightAfterReading() {
        val text = devotional(devo = "Body.", activity = "Amen.").toShareText(copyright = "  © Publisher.\n")

        assertEquals(
            "The Seed of the Woman\n\n" +
                "I will put enmity… between thy seed and her seed. (Genesis 3:15)\n\n" +
                "Body.\n\nAmen.\n\n© Publisher.",
            text
        )
    }

    @Test
    fun omitsTrailingBlankLinesWhenActivityIsEmpty() {
        val text = devotional(devo = "Body.", activity = "").toShareText()

        assertEquals(
            "The Seed of the Woman\n\n" +
                "I will put enmity… between thy seed and her seed. (Genesis 3:15)\n\n" +
                "Body.",
            text
        )
    }
}
