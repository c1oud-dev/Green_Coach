package com.application.frontend.model

// CO2 차트용 도메인
data class Co2Point(val year: Int, val value: Double)
data class Co2Series(val label: String, val points: List<Co2Point>)
data class Co2Snapshot(val emissions: Co2Series, val reduction: Co2Series)

// 성장 단계 (6단계)
enum class ForestStage(val title: String, val minShots: Int, val maxShots: Int?) {
    SEED("Seed", 0, 5),          // 0~5
    SPROUT("Sprout", 6, 10),     // 6~10
    SAPLING("Sapling", 11, 15),  // 11~15
    GROWING("Growing Tree", 16, 20), // 16~20
    MATURE("Mature Tree", 21, 30), // 21~30
    FRUIT("Fruit-bearing Tree", 31, null); // 31+

    companion object {
        fun fromShots(count: Int): ForestStage = when {
            count <= 5  -> SEED
            count <= 10 -> SPROUT
            count <= 15 -> SAPLING
            count <= 20 -> GROWING
            count <= 30 -> MATURE
            else        -> FRUIT
        }
    }
}