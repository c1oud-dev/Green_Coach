package com.application.frontend.model

// CO2 차트용 도메인
data class Co2Point(val year: Int, val value: Double)
data class Co2Series(val label: String, val points: List<Co2Point>)
data class Co2Snapshot(val emissions: Co2Series, val reduction: Co2Series)

// 성장 단계 (6단계)
enum class ForestStage(val title: String, val minShots: Int, val maxShots: Int?) {
    SEED("Seed", 0, 10),
    SPROUT("Sprout", 11, 20),
    SAPLING("Sapling", 21, 30),
    GROWING("Growing Tree", 31, 50),
    MATURE("Mature Tree", 51, 70),
    FRUIT("Fruit-bearing Tree", 71, null);

    companion object {
        fun fromShots(count: Int): ForestStage = when {
            count <= 10  -> SEED          // 0~10
            count <= 20  -> SPROUT        // 11~20
            count <= 30  -> SAPLING       // 21~30
            count <= 50  -> GROWING       // 31~50
            count <= 70  -> MATURE        // 51~70
            else         -> FRUIT         // 71+
        }

        fun fromLeafs(total: Int): ForestStage = when {
            total <= 10  -> SEED
            total <= 20  -> SPROUT
            total <= 30  -> SAPLING
            total <= 50  -> GROWING
            total <= 70  -> MATURE
            else         -> FRUIT
        }
    }
}