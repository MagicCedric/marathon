package com.malinskiy.marathon.report.debug.timeline

data class Data(val testName: String,
                val metricType: MetricType,
                val startDate: Long,
                val endDate: Long,
                val expectedValue: Double,
                val variance: Double)
