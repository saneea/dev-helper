package io.github.saneea.dvh.feature.multi

class FeatureTree(
    val alias: String,
    val description: String
) {
    val children: MutableList<FeatureTree> = ArrayList()
}