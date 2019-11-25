package thadeshammer

import org.openimaj.image.ImageUtilities
import org.openimaj.image.MBFImage
import org.openimaj.image.pixel.statistics.HistogramModel


import thadeshammer.experimentalcode.WaldoFinder
import thadeshammer.experimentalcode.WaldoHistogramFinder
import thadeshammer.experimentalcode.WaldoUtil
import thadeshammer.model.Model
import thadeshammer.model.ModelComponent

import java.io.File

fun main(args: Array<String>) {
    println("Hello, World")

    var waldoSourceImage: MBFImage = ImageUtilities.readMBF(File("C:\\Users\\thade\\Downloads\\waldo\\wm2.bmp"))

    /*
        naive RedBlue component model
     */

    val waldoRedComponent = ModelComponent(
        name = "waldoRedComponent",
        originalImage = waldoSourceImage,
        pixelTransforms = listOf(
            WaldoUtil.redComponentPPv1
        ),
        imageTransforms = listOf()
    )

    val waldoBlueComponent = ModelComponent(
        name = "waldoRedComponent",
        originalImage = waldoSourceImage,
        pixelTransforms = listOf(
            WaldoUtil.blueComponentPPv1
        ),
        imageTransforms = listOf()
    )

    val modelRB = Model(
        name = "waldoModel1",
        components = listOf(
            waldoRedComponent,
            waldoBlueComponent
        )
    )

//    println("=== VS RAW IMAGE ===")
//    val finderVSRawImage = WaldoFinder(modelRB)
//    finderVSRawImage.scanImageConcurrent(
//        ImageUtilities.readMBF(File("C:\\Users\\thade\\Downloads\\waldo\\waldo-7.bmp"))
//    )
//
//    println("=== VS RB COMPONENT TRANSFORMED IMAGE ===")
//    val finderVSComponentTransformImage = WaldoFinder(modelRB)
//    val redImage = ImageUtilities
//        .readMBF(File("C:\\Users\\thade\\Downloads\\waldo\\waldo-7.bmp"))
//        .processInplace(WaldoUtil.redComponentPPv1)
//    val blueImage = ImageUtilities
//        .readMBF(File("C:\\Users\\thade\\Downloads\\waldo\\waldo-7.bmp"))
//        .processInplace(WaldoUtil.blueComponentPPv1)
//    val componentTransformImage = redImage.subtract(blueImage).abs()
//
//    finderVSComponentTransformImage.scanImageConcurrent(
//        componentTransformImage
//    )

    /*
        So this finds Waldo at rank #16     :(
        16: ScoreKeeper(score=0.5593389518050591, x=832, y=564, width=102, height=188)
     */
    println("=== VS RAW-GEN OVERFIT HISTOGRAM ===")
    val hm = HistogramModel(
        waldoSourceImage.width,
        waldoSourceImage.height,
        3
    )
    hm.estimateModel(waldoSourceImage)
    val rawGenHistogramOverFit = hm.histogram

    val histoOverFitFinder = WaldoHistogramFinder(
        rawGenHistogramOverFit,
        waldoSourceImage.width, waldoSourceImage.height,
        Triple(waldoSourceImage.width, waldoSourceImage.height, 3)
    )
    histoOverFitFinder.scanImage(
        ImageUtilities.readMBF(File("C:\\Users\\thade\\Downloads\\waldo\\waldo-7.bmp"))
    )

    println("=== VS RAW-GEN CANONICAL-STEVE HISTOGRAM ===")
    val hm2 = HistogramModel(
        4,
        4,
        3
    )
    hm2.estimateModel(waldoSourceImage)
    val rawGenHistogram = hm2.histogram

    val histoFinder = WaldoHistogramFinder(
        rawGenHistogram,
        waldoSourceImage.width, waldoSourceImage.height,
        Triple(4, 4, 3)
    )
    histoFinder.scanImage(
        ImageUtilities.readMBF(File("C:\\Users\\thade\\Downloads\\waldo\\waldo-7.bmp"))
    )

    /*
        TODO figure out proportions of waldo in images (i.e. find waldo and boundbox him
        what is ratio of bound box dimensions to full image dimensions...scale model to each
        image? build a scaled image and work from there
     */

    /*
        first target image is source of model
        "C:\Users\thade\Downloads\waldo\waldo-7.bmp"
     */

    // 854, 560 is roughly where Waldo is in the trivial case
}

