package thadeshammer.experimentalcode

import org.openimaj.image.MBFImage
import org.openimaj.image.pixel.statistics.HistogramModel
import org.openimaj.image.processor.PixelProcessor
import org.openimaj.math.geometry.shape.Rectangle
import org.openimaj.math.statistics.distribution.MultidimensionalHistogram
import kotlin.math.abs

/**
 * thade created on 11/24/2019
 */
object WaldoUtil {
    const val RED = 0
    const val GREEN = 1
    const val BLUE = 2

    val blueComponentPPv1 = PixelProcessor<Array<Float>> {
        if (tooClose(it[BLUE],
                it[RED],
                0.3f)) {
            it[BLUE] = 0.0f
        } else {
            it[BLUE] = bias(it[BLUE], 0.7f)
        }

        it[RED] = 0.0f
        it[GREEN] = 0.0f

        it
    }

    val redComponentPPv1 = PixelProcessor<Array<Float>> {
        if (tooClose(it[WaldoUtil.RED],
                it[WaldoUtil.BLUE],
                0.3f)) {
            it[WaldoUtil.RED] = 0.0f
        } else {
            it[WaldoUtil.RED] = bias(it[WaldoUtil.RED], 0.95f)
        }

        it[WaldoUtil.GREEN] = 0.0f
        it[WaldoUtil.BLUE] = 0.0f

        it
    }

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

    fun rectOverlap(rect1: Rectangle, rect2: Rectangle): Boolean {
        // https://stackoverflow.com/questions/306316/determine-if-two-rectangles-overlap-each-other
        return (rect1.topLeft.x < rect2.bottomRight.x
            && rect1.bottomRight.x > rect2.topLeft.x
            && rect1.topLeft.y < rect2.bottomRight.y
            && rect1.bottomRight.y > rect2.topLeft.y)
    }

    fun thoroughTrimScoreList(scoreList: List<ScoreKeeper>): List<ScoreKeeper> {
        if (scoreList.isEmpty()) {
            return listOf()
        } else if (scoreList.size == 1) {
            return scoreList
        }

        var xSort = trimScoreList(scoreList.sortedBy { it.x })
        var ySort = trimScoreList(xSort.sortedBy { it.y })
        return trimScoreList(ySort.sortedBy { it.score })
    }

    fun trimScoreList(sortedScoreList: List<ScoreKeeper>): List<ScoreKeeper> {
        if (sortedScoreList.isEmpty()) {
            return listOf()
        }

        var trimmedScoreList: List<ScoreKeeper> = listOf()

        var localBest = sortedScoreList.first()
        for (i in 1 until sortedScoreList.size) {
            if (!rectOverlap(
                    Rectangle(
                        localBest.x.toFloat(), localBest.y.toFloat(),
                        localBest.width.toFloat(), localBest.height.toFloat()
                    ),
                    Rectangle(
                        sortedScoreList[i].x.toFloat(), sortedScoreList[i].y.toFloat(),
                        sortedScoreList[i].width.toFloat(), sortedScoreList[i].height.toFloat()
                    )
                )) {
                localBest = sortedScoreList[i]
                trimmedScoreList += localBest
            }
        }

        return trimmedScoreList
    }

    fun buildHistogram(image: MBFImage, histogramDimensions: Triple<Int, Int, Int>): MultidimensionalHistogram {
        val sourceImageHMM = HistogramModel(
            histogramDimensions.first,
            histogramDimensions.second,
            histogramDimensions.third
        )
        sourceImageHMM.estimateModel(image)
        return sourceImageHMM.histogram
    }

    fun runHistogramScan(sourceImage: MBFImage,
                         targetImage: MBFImage,
                         histogramDimensions: Triple<Int, Int, Int>,
                         scanStep: Int = 4,
                         wheresWaldo: Rectangle? = null) {

        val sourceImageMDH = buildHistogram(sourceImage, histogramDimensions)

        val histogramBasedFinder = WaldoHistogramFinder(
            sourceImageMDH,
            sourceImage.width,
            sourceImage.height,
            histogramDimensions
        )

        histogramBasedFinder.scanImage(targetImage, scanStep, wheresWaldo)
    }
}