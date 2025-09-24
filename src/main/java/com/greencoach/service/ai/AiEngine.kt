package com.greencoach.service.ai

import com.greencoach.model.scan.Prediction
import org.springframework.web.multipart.MultipartFile

/**
 * 모든 AI 엔진이 따라야 하는 공통 인터페이스
 */
interface AiEngine {
    val modelName: String

    /**
     * 이미지 파일을 받아서 예측 결과 반환
     */
    fun predict(file: MultipartFile): List<Prediction>
}