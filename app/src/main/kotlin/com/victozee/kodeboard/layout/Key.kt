package com.victozee.kodeboard.layout

import com.victozee.kodeboard.layout.builder.KeyInfo

class Key {
    // lateinit matches Java's null-before-assignment semantics while keeping non-null type.
    // Alternative for Java interop would be `var box: Box? = null` / `var info: KeyInfo? = null`.
    lateinit var box: Box
    lateinit var info: KeyInfo
}
