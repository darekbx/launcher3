package com.darekbx.launcher3.airly.domain

data class ResponseWrapper<T>(val value: T?, val hasError: Boolean = false) {

    companion object {
        fun <T> failed() = ResponseWrapper<T>(null, hasError = true)
    }
}
