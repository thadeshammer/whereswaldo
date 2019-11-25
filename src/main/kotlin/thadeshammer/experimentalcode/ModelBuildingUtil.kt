package thadeshammer.experimentalcode

import kotlin.math.abs

/**
 * thade created on 11/24/2019
 */
object ModelBuildingUtil {
    const val RED = 0
    const val GREEN = 1
    const val BLUE = 2

    fun bias(value: Float, limit: Float): Float {
        return if (value < limit) {
            0.0f
        } else {
            1.0f
        }
    }

    fun tooClose(value: Float, other: Float, limit: Float): Boolean {
        return (abs(value - other) <= limit)
    }
}