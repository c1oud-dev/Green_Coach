package com.application.frontend.data.co2

import com.application.frontend.model.Co2Point
import com.application.frontend.model.Co2Series
import com.application.frontend.model.Co2Snapshot

fun Co2SnapshotDto.toDomain(): Co2Snapshot =
    Co2Snapshot(
        emissions = Co2Series(
            label = emissions.label,
            points = emissions.points.map { Co2Point(it.year, it.value) }
        ),
        reduction = Co2Series(
            label = reduction.label,
            points = reduction.points.map { Co2Point(it.year, it.value) }
        )
    )