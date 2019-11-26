package thadeshammer

import org.openimaj.image.ImageUtilities
import thadeshammer.experimentalcode.ModelTestCases

import java.io.File

fun main(args: Array<String>) {
    println("Hello, World")

    val waldoSourceImage = ImageUtilities.readMBF(File("C:\\Users\\thade\\Downloads\\waldo\\wm2.bmp"))
    val waldoTargetImage = ImageUtilities.readMBF(File("C:\\Users\\thade\\Downloads\\waldo\\waldo-7.bmp"))

    ModelTestCases.trivialCasesModelVsModel(waldoSourceImage, waldoTargetImage)
    ModelTestCases.naiveRedBlueComponentCases(waldoSourceImage, waldoTargetImage)
    ModelTestCases.histogramVsRawImageTests(waldoSourceImage, waldoTargetImage)

    /*
        TODO figure out proportions of waldo in images (i.e. find waldo and boundbox him
        what is ratio of bound box dimensions to full image dimensions...scale model to each
        image? build a scaled image and work from there
     */

    // 855, 563 is roughly where Waldo is in the trivial case
}

/**
 * ROOM FOR IMPROVEMENT
 *
 * The compare method ought to either be overridable or plug-in play like the
 * component pieces.
 *
 * There are other permutations I ought to be using to cast a wider net,
 * e.g. slight scaling and rotations.
 *
 * A database or at least some kind of disk write-out of test results so I can
 * track improvements on accuracy and exec time
 *
 * Everything (models, samples, prepared target images) should be built up
 * just like the Model/ModelComponent structures where the permutations are
 * tracked and processed in the data structure (so we can reference the later,
 * track metrics on them eventually, toggle them and rebake the model, maybe).
 *
 * More fully leveraging OpenImaj is definitely within scope here: its feature
 * extraction would probably nail Waldo far better than my undergrad algorithm.
 *
 */


