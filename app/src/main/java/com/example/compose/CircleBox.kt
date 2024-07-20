package com.example.compose


import android.util.Log
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.LayoutScopeMarker
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.ParentDataModifierNode
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import java.lang.Math.min
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin


@Composable
inline fun CircleBox(
    modifier: Modifier = Modifier,
    propagateMinConstraints: Boolean = false,
    content: @Composable CircleBoxScope.() -> Unit
) {

    var rotateDegree by remember {
        mutableFloatStateOf(0F)
    }

    val measurePolicy = rememberSwipeRefreshMeasurePolicy(Alignment.Center, propagateMinConstraints,rotateDegree)

    Layout(
        content = { CircleBoxScopeInstance.content() },
        measurePolicy = measurePolicy,
        modifier = modifier then Modifier.pointerInput("CircleBoxInputEvent"){
            var startDegree = 0F

            detectDragGestures { change, dragAmount ->
                val dr = atan2(change.position.y.toDouble() - size.height/2f, change.position.x.toDouble() - size.width/2f);
                var toFloat = (dr - startDegree).toFloat()
                if(toFloat == Float.POSITIVE_INFINITY || toFloat == Float.NEGATIVE_INFINITY){
                    toFloat = 0F
                }
                rotateDegree +=  toFloat
                startDegree = dr.toFloat();
            }
        }
    )
}

@PublishedApi
@Composable
internal fun rememberSwipeRefreshMeasurePolicy(
    alignment: Alignment,
    propagateMinConstraints: Boolean,
    rotateDegree: Float
) =  remember(alignment, propagateMinConstraints,rotateDegree) {
    circleBoxMeasurePolicy(alignment, propagateMinConstraints,rotateDegree)
}

internal class CircleBoxMeasurePolicy (
     var alignment: Alignment,
     var propagateMinConstraints: Boolean = false,
     var rotateDegree: Float = 0F
): MeasurePolicy {
    override fun MeasureScope.measure(
        measurables: List<Measurable>,
        constraints: Constraints
    ): MeasureResult {

        Log.d(TAG,"rotateDegree = $rotateDegree")

        if (measurables.isEmpty()) {
            return layout(
                constraints.minWidth,
                constraints.minHeight
            ) {}
        }

        val contentConstraints = if (propagateMinConstraints) {
            constraints
        } else {
            constraints.copy(minWidth = 0, minHeight = 0)
        }

        if (measurables.size == 1) {
            val measurable = measurables[0]
            val boxWidth: Int
            val boxHeight: Int
            val placeable: Placeable
            if (!measurable.matchesParentSize) {
                placeable = measurable.measure(contentConstraints)
                boxWidth = max(constraints.minWidth, placeable.width)
                boxHeight = max(constraints.minHeight, placeable.height)
            } else {
                boxWidth = constraints.minWidth
                boxHeight = constraints.minHeight
                placeable = measurable.measure(
                    Constraints.fixed(constraints.minWidth, constraints.minHeight)
                )
            }
            return layout(boxWidth, boxHeight) {
                placeInBox(placeable, measurable, layoutDirection, boxWidth, boxHeight, alignment)
            }
        }

        val placeables = arrayOfNulls<Placeable>(measurables.size)
        // First measure non match parent size children to get the size of the Box.
        var boxWidth = constraints.minWidth
        var boxHeight = constraints.minHeight
        measurables.forEachIndexed { index, measurable ->
            if (!measurable.matchesParentSize) {
                val placeable = measurable.measure(contentConstraints)
                placeables[index] = placeable
                boxWidth = max(boxWidth, placeable.width)
                boxHeight = max(boxHeight, placeable.height)
            }
        }


        val radian = Math.toRadians((360 / placeables.size).toDouble()) ;
        val radius = min(constraints.minWidth, constraints.minHeight) / 2;
        // Specify the size of the Box and position its children.
       return layout(boxWidth, boxHeight) {
            placeables.forEachIndexed { index, placeable ->
                placeable as Placeable
                val innerRadius = radius - max(placeable.height,placeable.width);
                val x = cos(radian * index + rotateDegree) * innerRadius + boxWidth / 2F - placeable.width / 2F;
                val y = sin(radian * index + rotateDegree) * innerRadius + boxHeight / 2F - placeable.height / 2F;
                placeable.place(IntOffset(x.toInt(), y.toInt()))

            }
        }
    }

}
internal fun circleBoxMeasurePolicy(
    alignment: Alignment,
    propagateMinConstraints: Boolean,
    rotateDegree: Float
) =
    CircleBoxMeasurePolicy(alignment,propagateMinConstraints,rotateDegree)


@Composable
fun CircleBox(modifier: Modifier) {
    Layout({}, measurePolicy = EmptyBoxMeasurePolicy, modifier = modifier)
}

internal val EmptyBoxMeasurePolicy = MeasurePolicy { _, constraints ->
    layout(constraints.minWidth, constraints.minHeight) {}
}

@LayoutScopeMarker
@Immutable
interface CircleBoxScope {
    @Stable
    fun Modifier.align(alignment: Alignment): Modifier
    @Stable
    fun Modifier.matchParentSize(): Modifier
}

internal object CircleBoxScopeInstance : CircleBoxScope {
    @Stable
    override fun Modifier.align(alignment: Alignment) = this.then(
        CircleBoxChildDataElement(
            alignment = alignment,
            matchParentSize = false,
            inspectorInfo = debugInspectorInfo {
                name = "align"
                value = alignment
            }
        ))

    @Stable
    override fun Modifier.matchParentSize() = this.then(
        CircleBoxChildDataElement(
            alignment = Alignment.Center,
            matchParentSize = true,
            inspectorInfo = debugInspectorInfo {
                name = "matchParentSize"
            }
        ))
}

private val Measurable.boxChildDataNode: CircleBoxChildDataNode? get() = parentData as? CircleBoxChildDataNode
private val Measurable.matchesParentSize: Boolean get() = boxChildDataNode?.matchParentSize ?: false

private class CircleBoxChildDataElement(
    val alignment: Alignment,
    val matchParentSize: Boolean,
    val inspectorInfo: InspectorInfo.() -> Unit

) : ModifierNodeElement<CircleBoxChildDataNode>() {
    override fun create(): CircleBoxChildDataNode {
        return CircleBoxChildDataNode(alignment, matchParentSize)
    }

    override fun update(node: CircleBoxChildDataNode) {
        node.alignment = alignment
        node.matchParentSize = matchParentSize
    }

    override fun InspectorInfo.inspectableProperties() {
        inspectorInfo()
    }

    override fun hashCode(): Int {
        var result = alignment.hashCode()
        result = 31 * result + matchParentSize.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        val otherModifier = other as? CircleBoxChildDataElement ?: return false
        return alignment == otherModifier.alignment &&
                matchParentSize == otherModifier.matchParentSize
    }
}

private fun Placeable.PlacementScope.placeInBox(
    placeable: Placeable,
    measurable: Measurable,
    layoutDirection: LayoutDirection,
    boxWidth: Int,
    boxHeight: Int,
    alignment: Alignment
) {
    val childAlignment = measurable.boxChildDataNode?.alignment ?: alignment
    val position = childAlignment.align(
        IntSize(placeable.width, placeable.height),
        IntSize(boxWidth, boxHeight),
        layoutDirection
    )
    placeable.place(position)
}

private class CircleBoxChildDataNode(
    var alignment: Alignment,
    var matchParentSize: Boolean,
) : ParentDataModifierNode, Modifier.Node() {
    override fun Density.modifyParentData(parentData: Any?) = this@CircleBoxChildDataNode
}
