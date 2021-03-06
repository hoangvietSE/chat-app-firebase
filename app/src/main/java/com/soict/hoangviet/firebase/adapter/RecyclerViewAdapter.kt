package com.soict.hoangviet.firebase.adapter

import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.soict.hoangviet.firebase.utils.LogUtil
import java.util.concurrent.atomic.AtomicInteger

abstract class RecyclerViewAdapter(var context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        const val VIEW_TYPE_NORMAL = 0
        var idGenerator: AtomicInteger = AtomicInteger()
    }

    protected var mRecyclerView: RecyclerView? = null
    protected var mListWrapperModel: MutableList<WrapperModel> = mutableListOf()
    protected var mListWrapperModelBackup: MutableList<WrapperModel> = mutableListOf()
    protected var mOnItemPressListener: OnItemPressListener? = null
    protected var mOnItemCancelListener: OnItemCancelListener? = null
    protected var mOnItemClickListener: ((parent: ViewGroup, viewType: Int, view: View, position: Int?) -> Unit)? = null

    protected fun backUp() {
        mListWrapperModelBackup.clear()
        for (model in mListWrapperModel) {
            try {
                mListWrapperModelBackup.add(model.clone())
            } catch (exception: CloneNotSupportedException) {
                LogUtil.e(exception)
            }
        }
    }

    protected fun recoverModel() {
        val mDiffUtilCallBack = DiffUtilCallBack(mListWrapperModel, mListWrapperModelBackup)
        val mDiffResult = DiffUtil.calculateDiff(mDiffUtilCallBack)
        mListWrapperModel.clear()
        mListWrapperModelBackup.addAll(mListWrapperModelBackup)
        //notify for recycler view to update
        mDiffResult.dispatchUpdatesTo(this)
    }

    fun setOnItemPressListener(listener: OnItemPressListener) {
        mOnItemPressListener = listener
    }

    fun setOnItemCancelListener(listener: OnItemCancelListener) {
        mOnItemCancelListener = listener
    }

    fun setOnItemClickListener(
        listener: ((parent: ViewGroup, viewType: Int, view: View, position: Int?) -> Unit)?
    ) {
        mOnItemClickListener = listener
    }

    fun clear() {
        mListWrapperModel.clear()
        notifyDataSetChanged()
    }

    protected fun refreshModel(listModel: List<Any?>) {
        refreshModel(listModel, VIEW_TYPE_NORMAL)
    }

    protected fun refreshModel(listModel: List<Any?>, viewType: Int) {
        refreshModel(listModel, viewType, false)
    }


    protected fun refreshModel(listModel: List<Any?>, viewType: Int, isScroolToLast: Boolean) {
        clear()
        addModel(listModel, viewType, isScroolToLast)
    }

    fun addModel(model: Any?) {
        addModel(model, VIEW_TYPE_NORMAL)
    }

    fun addModel(model: Any?, viewType: Int) {
        addModel(model, viewType, false)
    }

    fun addModel(model: Any?, viewType: Int, isSelected: Boolean) {
        addModel(model, viewType, isSelected, false)
    }

    fun addModel(model: Any?, viewType: Int, isSelected: Boolean, isScroolToLast: Boolean) {
        val wrapperModel = WrapperModel(model, viewType)
        wrapperModel.isSelected = isSelected
        mListWrapperModel.add(wrapperModel)
        notifyItemInserted(itemCount - 1)
        if (isScroolToLast) {
            mRecyclerView?.smoothScrollToPosition(itemCount - 1)
        }
    }

    fun addModels(listModel: List<Any?>, isScroolToLast: Boolean) {
        addModels(listModel, VIEW_TYPE_NORMAL, isScroolToLast)
    }

    fun addModels(listModels: List<Any?>, viewType: Int, isScroolToLast: Boolean) {
        val listModel: MutableList<WrapperModel> = mutableListOf()
        for (model in listModels) {
            val mWrapperModel = WrapperModel(model, viewType)
            listModel.add(mWrapperModel)
        }
        val lastSize = mListWrapperModel.size
        mListWrapperModel.addAll(listModel)
        notifyItemRangeInserted(lastSize, mListWrapperModel.size - lastSize)
        if (isScroolToLast) {
            mRecyclerView?.smoothScrollToPosition(mListWrapperModel.size - 1)
        }
    }

    fun removeItemAt(position: Int) {
        mListWrapperModel.removeAt(position)
        notifyItemRemoved(position)
    }

    protected fun setItemSelected(position: Int, selected: Boolean) {
        if (mListWrapperModel.size > 0 && position < mListWrapperModel.size - 1) {
            mListWrapperModel[position].isSelected = selected
            notifyItemChanged(position)
        }
    }

    protected fun showAllUnSelectedItem(listener: OnItemSelectedListener) {
        mListWrapperModel.forEach {
            if (!it.isSelected) {
                listener.onEachItemUnSelected(it)
            }
        }
    }

    protected fun removeAllItemSelected() {
        val listItem: MutableList<WrapperModel> = mutableListOf()
        if (mListWrapperModel.size > 0) {
            showAllUnSelectedItem(object : OnItemSelectedListener {
                override fun onEachItemUnSelected(wrapperModel: WrapperModel) {
                    listItem.add(wrapperModel)
                }
            })
        }
        val mDiffUtilCallBack = DiffUtilCallBack(mListWrapperModel, listItem)
        val diffResult = DiffUtil.calculateDiff(mDiffUtilCallBack)
        mListWrapperModel = listItem
        diffResult.dispatchUpdatesTo(this@RecyclerViewAdapter)
    }

    override fun getItemViewType(position: Int): Int {
        return mListWrapperModel[position].viewType
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        this.mRecyclerView = recyclerView
        super.onAttachedToRecyclerView(recyclerView)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        this.mRecyclerView = null
        super.onDetachedFromRecyclerView(recyclerView)
    }

    fun <T> getItemPosition(position: Int, classOfT: Class<T>): T {
        return classOfT.cast(mListWrapperModel[position].model)
    }

    private fun notifyItemClickListener(
        parent: ViewGroup,
        viewType: Int,
        view: View,
        position: Int?
    ) {
        mOnItemClickListener?.invoke(parent, viewType, view, position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewHolder = solveOnCreateViewHolder(parent, viewType)
        viewHolder?.itemView?.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    mOnItemPressListener?.onItemPress(view, getViewByPosition(view))
                    true
                }
                MotionEvent.ACTION_CANCEL -> {
                    mOnItemCancelListener?.onItemCancel(view, getViewByPosition(view))
                    true
                }
                /**
                 * clickable=true
                 * focusable=true
                 */
                MotionEvent.ACTION_UP -> {
                    notifyItemClickListener(parent, viewType, view, getViewByPosition(view))
                    true
                }
            }
            false
        }
        return viewHolder!!
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        solveOnBindViewHolder(holder, position)
    }

    protected fun getViewByPosition(view: View): Int? {
        return mRecyclerView?.getChildLayoutPosition(view)
    }

    override fun getItemCount(): Int {
        return mListWrapperModel.size
    }

    class WrapperModel(model: Any?, var viewType: Int = 0) : Cloneable {
        val id = idGenerator.getAndIncrement()
        var model: Any? = null
            internal set
        var isSelected: Boolean = false
            internal set

        init {
            this.model = model
        }

        override fun equals(other: Any?): Boolean {
            val mWrapperModel = other as WrapperModel
            if (this.model != mWrapperModel.model) {
                return false
            }
            if (this.viewType != mWrapperModel.viewType) {
                return false
            }
            return if (mWrapperModel.model == null) {
                this.model == null
            } else {
                this.model == mWrapperModel.model
            }
        }

        @Throws(CloneNotSupportedException::class)
        public override fun clone(): WrapperModel {
            return super.clone() as WrapperModel
        }
    }

    protected open fun solveOnCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder? {
        return if (viewType == VIEW_TYPE_NORMAL)
            initNormalViewHolder(parent, viewType)
        else
            null
    }

    protected open fun solveOnBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (mListWrapperModel[position].viewType == VIEW_TYPE_NORMAL) {
            bindNormalViewHolder(holder as NormalViewHolder, position)
        }
    }

    abstract fun initNormalViewHolder(parent: ViewGroup, viewType: Int): NormalViewHolder?

    abstract fun bindNormalViewHolder(holder: NormalViewHolder, position: Int)

    abstract class NormalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun <T> bind(data: T)
    }

    interface OnItemPressListener {
        fun onItemPress(view: View, position: Int?)
    }

    interface OnItemCancelListener {
        fun onItemCancel(view: View, position: Int?)
    }

    interface OnItemSelectedListener {
        fun onEachItemUnSelected(wrapperModel: WrapperModel)
    }
}