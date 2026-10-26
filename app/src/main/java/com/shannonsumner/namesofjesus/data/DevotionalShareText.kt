package com.shannonsumner.namesofjesus.data

/**
 * The devotional as plain text for sharing, laid out like the website's
 * `ShareButtons` text: topic, main scripture with its reference, the reading,
 * the closing prayer, and then [copyright] (plain text) when it isn't blank.
 * HTML has already been stripped and entities decoded.
 */
fun Devotional.toShareText(copyright: String = ""): String = buildList {
    add(topic)
    add("")
    add("$scripture ($reference)")
    add("")
    add(devo.paragraphs.joinToString("\n\n") { it.plainText })
    add("")
    addAll(activity.paragraphs.map { it.plainText })
}.joinToString("\n").trimEnd().let { reading ->
    if (copyright.isBlank()) reading else "$reading\n\n${copyright.trim()}"
}
