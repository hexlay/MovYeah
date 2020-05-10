package hexlay.movyeah.layouts

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import hexlay.movyeah.R
import kotlin.math.roundToInt

// https://github.com/cachapa/ExpandableLayout
class CustomExpandableLayout : FrameLayout {

    private val horizontal = 0
    private val vertical = 1

    private var duration = 300
    private var parallax = 0f
    private var expansion = 0f
    private var orientation = 0
    private var state = 0

    private var interpolator = FastOutSlowInInterpolator()
    private var animator: ValueAnimator? = null

    private var listener: OnExpansionUpdateListener? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        if (attrs != null) {
            val stylableAttribute = getContext().obtainStyledAttributes(attrs, R.styleable.CustomExpandableLayout)
            duration = stylableAttribute.getInt(R.styleable.CustomExpandableLayout_el_duration, 300)
            expansion = (if (stylableAttribute.getBoolean(R.styleable.CustomExpandableLayout_el_expanded, false)) 1 else 0).toFloat()
            orientation = stylableAttribute.getInt(R.styleable.CustomExpandableLayout_android_orientation, vertical)
            parallax = stylableAttribute.getFloat(R.styleable.CustomExpandableLayout_el_parallax, 1f)
            stylableAttribute.recycle()
            state = if (expansion == 0f) State.COLLAPSED else State.EXPANDED
            setParallax(parallax)
        }
    }


    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val bundle = Bundle()
        expansion = (if (isExpanded()) 1 else 0).toFloat()
        bundle.putFloat("expansion", expansion)
        bundle.putParcelable("super_state", superState)
        return bundle
    }

    override fun onRestoreInstanceState(parcelable: Parcelable) {
        val bundle = parcelable as Bundle
        expansion = bundle.getFloat("expansion")
        state = if (expansion == 1f) State.EXPANDED else State.COLLAPSED
        val superState = bundle.getParcelable<Parcelable>("super_state")
        super.onRestoreInstanceState(superState)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = measuredWidth
        val height = measuredHeight
        val size = if (orientation == LinearLayout.HORIZONTAL) width else height
        visibility = if (expansion == 0f && size == 0) View.GONE else View.VISIBLE
        val expansionDelta = size - (size * expansion).roundToInt()
        if (parallax > 0) {
            val parallaxDelta = expansionDelta * parallax
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                if (orientation == horizontal) {
                    var direction = -1
                    if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                        direction = 1
                    }
                    child.translationX = direction * parallaxDelta
                } else {
                    child.translationY = -parallaxDelta
                }
            }
        }
        if (orientation == horizontal) {
            setMeasuredDimension(width - expansionDelta, height)
        } else {
            setMeasuredDimension(width, height - expansionDelta)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        animator?.cancel()
        super.onConfigurationChanged(newConfig)
    }

    fun isExpanded(): Boolean {
        return state == State.EXPANDING || state == State.EXPANDED
    }

    fun toggle(animate: Boolean = true) {
        if (isExpanded()) {
            collapse(animate)
        } else {
            expand(animate)
        }
    }

    fun expand(animate: Boolean = true) {
        setExpanded(true, animate)
    }

    fun collapse(animate: Boolean = true) {
        setExpanded(false, animate)
    }

    private fun setExpanded(expand: Boolean, animate: Boolean) {
        if (expand == isExpanded()) {
            return
        }
        val targetExpansion = if (expand) 1 else 0
        if (animate) {
            animateSize(targetExpansion)
        } else {
            setExpansion(targetExpansion.toFloat())
        }
    }

    fun setExpansion(expansion: Float) {
        if (this.expansion == expansion) {
            return
        }

        val delta = expansion - this.expansion
        when {
            expansion == 0f -> {
                state = State.COLLAPSED
            }
            expansion == 1f -> {
                state = State.EXPANDED
            }
            delta < 0 -> {
                state = State.COLLAPSING
            }
            delta > 0 -> {
                state = State.EXPANDING
            }
        }
        visibility = if (state == State.COLLAPSED) View.INVISIBLE else View.VISIBLE
        this.expansion = expansion
        requestLayout()
        listener?.onExpansionUpdate(expansion, state)
    }

    private fun setParallax(parallax: Float) {
        this.parallax = 1f.coerceAtMost(0f.coerceAtLeast(parallax))
    }

    fun setOnExpansionUpdateListener(listener: (fraction: Float, state: Int) -> Unit) {
        this.listener = object : OnExpansionUpdateListener {
            override fun onExpansionUpdate(fraction: Float, state: Int) {
                listener(fraction, state)
            }
        }
    }

    private fun animateSize(targetExpansion: Int) {
        if (animator != null) {
            animator!!.cancel()
            animator = null
        }
        animator = ValueAnimator.ofFloat(expansion, targetExpansion.toFloat())
        animator!!.interpolator = interpolator
        animator!!.duration = duration.toLong()
        animator!!.addUpdateListener { valueAnimator -> setExpansion(valueAnimator.animatedValue as Float) }
        animator!!.addListener(object : Animator.AnimatorListener {
            private var canceled = false

            override fun onAnimationStart(animation: Animator) {
                state = if (targetExpansion == 0) State.COLLAPSING else State.EXPANDING
            }

            override fun onAnimationEnd(animation: Animator) {
                if (!canceled) {
                    state = if (targetExpansion == 0) State.COLLAPSED else State.EXPANDED
                    setExpansion(targetExpansion.toFloat())
                }
            }

            override fun onAnimationCancel(animation: Animator) {
                canceled = true
            }

            override fun onAnimationRepeat(animation: Animator) {}
        })
        animator!!.start()
    }

    object State {
        const val COLLAPSED = 0
        const val COLLAPSING = 1
        const val EXPANDING = 2
        const val EXPANDED = 3
    }

    interface OnExpansionUpdateListener {
        fun onExpansionUpdate(fraction: Float, state: Int)
    }

}