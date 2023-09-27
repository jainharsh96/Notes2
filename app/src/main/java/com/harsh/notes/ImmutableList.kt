package com.harsh.notes

import androidx.compose.runtime.Stable


// todo create immutable list wrapper
@Stable
class ImmutableList<out T : List<T>>(val list : T)