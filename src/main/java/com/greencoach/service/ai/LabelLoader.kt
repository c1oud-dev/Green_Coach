package com.greencoach.service.ai

import java.io.InputStreamReader

object LabelLoader {
    fun loadLabels(resourcePath: String = "/models/classes.txt"): List<String> {
        val stream = javaClass.getResourceAsStream(resourcePath)
            ?: throw IllegalStateException("Labels not found: $resourcePath")
        return InputStreamReader(stream).readLines()
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }
}