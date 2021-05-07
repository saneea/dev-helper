package io.github.saneea.dvh.feature.multi

import io.github.saneea.dvh.FeatureContext
import io.github.saneea.dvh.FeatureProvider
import io.github.saneea.dvh.utils.Utils
import java.io.PrintStream
import java.util.function.ToIntFunction
import java.util.stream.Collectors
import java.util.stream.IntStream

class TreePrinter(out: PrintStream) : FeatureCatalogPrinter(out) {

    override fun print(featureProvider: FeatureProvider, context: FeatureContext) {
        val featuresChain = context.featuresChain
            .map { featureName: String -> FeatureTree(featureName, "") }
            .collect(Collectors.toList())

        for (i in 0 until featuresChain.size - 1) {
            featuresChain[i].children.add(featuresChain[i + 1])
        }

        buildFeatureTree(featuresChain[featuresChain.size - 1], featureProvider, context)
        val root = featuresChain[0]
        val maxFeatureNameSize = getMaxFeatureNameSize(root, 0)
        printCatalogChildBranch("", root, maxFeatureNameSize, true, 0)
    }

    private fun getMaxFeatureNameSize(featureTree: FeatureTree, level: Int): Int {
        return IntStream.concat(
            IntStream.of(
                featureTree
                    .alias
                    .length + getLevelOffset(level)
            ),
            featureTree.children.stream()
                .map { childBranch: FeatureTree -> getMaxFeatureNameSize(childBranch, level + 1) }
                .mapToInt(identity()))
            .max()
            .asInt
    }

    private fun printCatalogTreeBranch(
        featureTree: FeatureTree, levelLineSuffix: String, level: Int,
        maxFeatureNameSize: Int
    ) {
        val features = featureTree.children
        for (i in features.indices) {
            printCatalogChildBranch(
                levelLineSuffix, features[i], maxFeatureNameSize, i == features.size - 1,
                level + 1
            )
        }
    }

    private fun printCatalogChildBranch(
        levelLineSuffix: String, feature: FeatureTree, maxFeatureNameSize: Int,
        last: Boolean, level: Int
    ) {
        val featureName = feature.alias
        val featureShortDescription = feature.description
        val isRoot = level == 0
        val isHub = feature.children.isNotEmpty()
        val featureLine = StringBuilder()
        featureLine.append(levelLineSuffix)
        if (!isRoot) {
            featureLine.append(if (last) "\\" else "+")
            featureLine.append(DASH_FOR_NODE)
        }
        featureLine.append(featureName)
        if (!isHub) {
            featureLine
                .append(Utils.repeatString(" ", maxFeatureNameSize - featureName.length - getLevelOffset(level)))
                .append(" - ")
                .append(featureShortDescription)
        }
        out.println(featureLine)
        if (isHub) {
            val nextLevelLineSuffix = StringBuilder(levelLineSuffix)
            if (!isRoot) {
                nextLevelLineSuffix.append(if (last) " " else "|")
                nextLevelLineSuffix.append(SPACE_FOR_NODE)
            }
            printCatalogTreeBranch(feature, nextLevelLineSuffix.toString(), level, maxFeatureNameSize)
        } else if (last) {
            out.println(levelLineSuffix)
        }
    }

    private fun getLevelOffset(level: Int): Int {
        return (DASH_LENGTH + 1) * level
    }

    private fun buildFeatureTree(parent: FeatureTree, featureProvider: FeatureProvider, context: FeatureContext) {
        val featuresNames = featureProvider.featuresNames()
        for (featureName in featuresNames) {
            val childContext = FeatureContext(context, featureName, Utils.NO_ARGS)
            val featureInfo = featureProvider.featureInfo(childContext)
            val child = FeatureTree(featureName, featureInfo.description)
            parent.children.add(child)
            if (featureInfo.feature is MultiFeature) {
                buildFeatureTree(child, featureInfo.feature.featureProvider, childContext)
            }
        }
    }

    companion object {
        private const val DASH_LENGTH = 3
        private const val DASH_STRING = "-"
        private const val SPACE_STRING = " "
        private val DASH_FOR_NODE = Utils.repeatString(DASH_STRING, DASH_LENGTH)
        private val SPACE_FOR_NODE = Utils.repeatString(SPACE_STRING, DASH_LENGTH)
        private fun identity(): ToIntFunction<Int> {
            return ToIntFunction { i: Int? -> i!! }
        }
    }
}