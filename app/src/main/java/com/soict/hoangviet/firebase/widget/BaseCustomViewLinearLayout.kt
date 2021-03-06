package com.soict.hoangviet.firebase.widget

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.widget.LinearLayout
import com.soict.hoangviet.firebase.extension.inflate

abstract class BaseCustomViewLinearLayout : LinearLayout {
    protected abstract var layoutRes: Int
    protected abstract var styleRes: IntArray?

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setLayout()
        initListener()
        initView()
        initData()
        attrs?.let {
            initStyleable(it)
        }
    }

    private fun initStyleable(it: AttributeSet) {
        val typeArray = context.obtainStyledAttributes(it, styleRes, 0, 0)
        initDataFromStyleable(typeArray)
        typeArray.recycle()
    }

    protected open fun initDataFromStyleable(typeArray: TypedArray?) {
    }

    protected open fun initView() {
    }

    protected open fun initData() {
    }

    protected open fun initListener() {
    }

    private fun setLayout() {
        context.inflate(layoutRes, this, true)
    }
}