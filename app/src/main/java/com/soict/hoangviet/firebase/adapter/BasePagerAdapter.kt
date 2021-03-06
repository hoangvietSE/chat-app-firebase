package com.soict.hoangviet.firebase.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter

abstract class BasePagerAdapter(val context: Context, val mListImage: ArrayList<String>) : PagerAdapter() {
    override fun isViewFromObject(view: View, `object`: Any) = view == `object` as View

    override fun getCount() = mListImage.size

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getItemPosition(`object`: Any) = POSITION_NONE
}