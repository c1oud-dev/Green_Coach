package com.greencoach.service

import com.greencoach.model.scan.ScanResponse
import com.greencoach.service.ai.AiEngine
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class ScanService(
    private val aiEngine: AiEngine
) {
    fun analyzeImage(file: MultipartFile): ScanResponse {
        val predictions = aiEngine.predict(file)
        return ScanResponse(
            items = predictions,
            model = aiEngine.modelName
        )
    }
}