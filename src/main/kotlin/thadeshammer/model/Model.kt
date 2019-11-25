package thadeshammer.model

import org.openimaj.image.MBFImage

/**
 * thade created on 11/23/2019
 */
data class Model(
    val name: String,
    val components: List<ModelComponent>
) {
    var fullModel = MBFImage(
        components[0].componentImage.width,
        components[0].componentImage.height
    ).fill(arrayOf(0.0f, 0.0f, 0.0f))

    var componentCount = 0

    init {
        components.forEach {
            // All components must have the same dimensions as the first.
            if (componentCount == 0 ||
                it.originalImage.width == fullModel.width &&
                it.originalImage.height == fullModel.height) {
                fullModel.add(it.originalImage)
                componentCount += 1
            }
        }
    }

    fun width(): Int { return fullModel.width }
    fun height(): Int { return fullModel.height }

    /**
     * Returns comparision score vs another Model; lower means nearer, 0.0 means perfect match.
     *
     * It's a simplistic Euclidean distance between the images, then summed up which obviously
     * has issues (possibly things that are nothing like Waldo may come across as Waldo if they
     * coincidently have similar scores after summing when they wouldn't still im matrix form).
     *
     * TODO try this
     */
    fun compare(other: Model): Double {
        return this.fullModel
            .subtract(other.fullModel)
            .abs()
            .doublePixelVector
            .sum()
    }

    fun compare(other: MBFImage): Double {
        return this.fullModel
            .subtract(other)
            .abs()
            .doublePixelVector
            .sum()
    }
}