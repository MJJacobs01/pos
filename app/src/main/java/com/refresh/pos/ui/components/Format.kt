package com.refresh.pos.ui.components

/** Presentational money formatting (does not change stored values). */
fun formatMoney(value: Double): String = "%.2f".format(value)
