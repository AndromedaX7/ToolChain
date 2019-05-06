package c.feature.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs

class SideBar : View {

    var textColor: Int = Color.BLACK
    var textSize: Float = 25f;
    var selectedColor: Int = Color.parseColor("#3399ff")
    var paintStyle: Paint.Style = Paint.Style.STROKE

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    ) {
        context?.let {
            val attr = it.obtainStyledAttributes(attrs, R.styleable.SideBar)
            textColor = attr.getColor(R.styleable.SideBar_textColor, textColor)
            textSize = attr.getDimension(R.styleable.SideBar_textSize, textSize)
            selectedColor = attr.getColor(R.styleable.SideBar_selectedColor, selectedColor)
            val mode = attr.getInt(R.styleable.SideBar_textDrawMode, 0)

            paintStyle = when (mode) {
                0 -> Paint.Style.FILL
                1 -> Paint.Style.STROKE
                2 -> Paint.Style.FILL_AND_STROKE
                else -> Paint.Style.FILL
            }
            attr.recycle()
        }
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val specModeWidth: Int = MeasureSpec.getMode(widthMeasureSpec);
        val specSizeWidth: Int = MeasureSpec.getSize(widthMeasureSpec)
        val mWidth = when (specModeWidth) {
            MeasureSpec.EXACTLY -> specSizeWidth
            MeasureSpec.AT_MOST -> (textSize * 2).toInt()
            else -> (textSize * 2).toInt()
        }
        val specModeHeight: Int = MeasureSpec.getMode(widthMeasureSpec);
        val specSizeHeight: Int = MeasureSpec.getSize(widthMeasureSpec)
        val mHeight = when (specModeHeight) {
            MeasureSpec.EXACTLY -> specSizeHeight
            MeasureSpec.AT_MOST -> specSizeHeight
            else ->specSizeHeight
        }

        setMeasuredDimension(mWidth, mHeight)

    }

    private var onTextTouchedListener: ((String) -> Unit)? = null

    fun setOnTextTouchedListener(l: (String) -> Unit) {
        onTextTouchedListener = l;
    }

    private var paint: Paint = Paint()

    init {
        paint.textSize = textSize
        paint.isAntiAlias = true
        paint.color = textColor
        paint.strokeWidth = 1f
        paint.style = paintStyle
        paint.textAlign = Paint.Align.LEFT


    }

    private var current: Int = -1
    private val rect: Rect = Rect();
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val charHeight = height * 1.0f / chars.size

        for (i in 0 until chars.size) {
            paint.textSize = textSize
            paint.isAntiAlias = true
            paint.color = textColor
            paint.strokeWidth = 1f
            paint.style = paintStyle
            paint.textAlign = Paint.Align.LEFT
            paint.getTextBounds(chars[i], 0, 1, rect)

            if (current == i) {
                paint.color = selectedColor
                paint.isFakeBoldText = true;
            }

            canvas!!.drawText(
                chars[i],
                (width - rect.right) * 1.0f / 2,
                (charHeight / 2 + ((abs(rect.top) + abs(rect.bottom)) / 2)) + charHeight * i,
                paint
            )
            paint.reset()
        }


    }


    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        val action = event!!.action
        val touchY = event.y;
        val preChooseIdx = current

        val currentTouchIdx = (touchY / height) * chars.size

        when (action) {
            MotionEvent.ACTION_UP -> {
                background = ColorDrawable(0x00000000);
                current = -1;
                invalidate();
            }
            else -> {
                background = ColorDrawable(0xFF0000)

                if (currentTouchIdx.toInt() != preChooseIdx) {
                    // 如果触摸点没有超出控件范围
                    if (currentTouchIdx >= 0 && currentTouchIdx < chars.size) {
                        onTextTouchedListener?.let {
                            it(chars[currentTouchIdx.toInt()])
                        }
                        current = currentTouchIdx.toInt()
                        invalidate();
                    }
                }
            }
        }


        return true
    }

    companion object {
        val chars = arrayOf(
            "A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z", "#"
        )
    }
}