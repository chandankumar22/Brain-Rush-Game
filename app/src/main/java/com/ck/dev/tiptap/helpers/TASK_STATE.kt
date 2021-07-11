package com.ck.dev.tiptap.helpers

sealed class TASK_STATE<String>(
    val msg: kotlin.String = ""
) {
    class PROGRESS(message:String): TASK_STATE<String>(message)
    class SUCCESS(message:String): TASK_STATE<String>(message)
    class FAILED(message:String): TASK_STATE<String>(message)
}