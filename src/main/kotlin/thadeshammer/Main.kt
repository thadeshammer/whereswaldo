package thadeshammer

import org.openimaj.image.ImageUtilities
import org.openimaj.image.MBFImage
import thadeshammer.experimentalcode.WaldoFinder
import thadeshammer.experimentalcode.WaldoUtil
import thadeshammer.model.Model
import thadeshammer.model.ModelComponent
import java.io.File

fun main(args: Array<String>) {
    println("Hello, World")

    var waldoSourceImage: MBFImage = ImageUtilities.readMBF(File("C:\\Users\\thade\\Downloads\\waldo\\wm2.bmp"))

//    var pixel = image.getPixel(0,0)
//
//    println("pixel size: ${pixel.size}")
//    println("pixel data: ${pixel.asList()}")

    //    DisplayUtilities.display(image)

//    val finder = playWithExperimentalModel(waldoModelSourceImage)
//    finder.doItBetter()

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

    println("=== VS RAW IMAGE ===")
    val finderVSRawImage = WaldoFinder(modelRB)
    finderVSRawImage.scanImageConcurrent(
        ImageUtilities.readMBF(File("C:\\Users\\thade\\Downloads\\waldo\\waldo-7.bmp"))
    )

    println("=== VS RB COMPONENT TRANSFORMED IMAGE ===")
    val finderVSComponentTransformImage = WaldoFinder(modelRB)
    val redImage = ImageUtilities
        .readMBF(File("C:\\Users\\thade\\Downloads\\waldo\\waldo-7.bmp"))
        .processInplace(WaldoUtil.redComponentPPv1)
    val blueImage = ImageUtilities
        .readMBF(File("C:\\Users\\thade\\Downloads\\waldo\\waldo-7.bmp"))
        .processInplace(WaldoUtil.blueComponentPPv1)
    val componentTransformImage = redImage.subtract(blueImage).abs()

    finderVSComponentTransformImage.scanImageConcurrent(
        componentTransformImage
    )


//    val testimg = ImageUtilities.readMBF(File("C:\\Users\\thade\\Downloads\\waldo\\waldo-7.bmp"))
//    val sample = testimg.extractROI(0,0,200,200)
//    DisplayUtilities.display(sample)

    /*
        TODO figure out proportions of waldo in images (i.e. find waldo and boundbox him
        what is ratio of bound box dimensions to full image dimensions...scale model to each
        image? build a scaled image and work from there
     */

    /*
        first target image is source of model
        "C:\Users\thade\Downloads\waldo\waldo-7.bmp"
     */

}

