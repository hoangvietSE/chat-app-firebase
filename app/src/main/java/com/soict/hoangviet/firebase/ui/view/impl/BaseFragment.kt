package com.soict.hoangviet.firebase.ui.view.impl

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.soict.hoangviet.firebase.common.BaseLoadingDialog
import com.soict.hoangviet.firebase.extension.inflate
import com.soict.hoangviet.firebase.ui.presenter.BasePresenter
import com.soict.hoangviet.firebase.ui.view.BaseView
import org.greenrobot.eventbus.EventBus

abstract class BaseFragment<P : BasePresenter> : Fragment(), BaseView {
    protected var parentActivity: AppCompatActivity? = null
    protected val mPresenter: P get() = getPresenter()
    abstract val mLayoutRes: Int

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BaseActivity<*>) {
            parentActivity = context
        }
        (parentActivity as BaseActivity<*>)?.onFragmentAttached()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return parentActivity?.inflate(mLayoutRes, container!!, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mPresenter.onAttach()
        initListener()
    }

    override fun showLoading() {
        parentActivity?.let {
            BaseLoadingDialog.getInstance(it).showLoadingDialog()
        }
    }

    override fun hideLoading() {
        parentActivity?.let {
            BaseLoadingDialog.getInstance(it).hideLoadingDialog()
        }
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDetach() {
        super.onDetach()
        (parentActivity as BaseActivity<*>).onFragmentDetached("")

    }

    abstract fun getPresenter(): P

    interface CallBack {
        fun onFragmentAttached()
        fun onFragmentDetached(tag: String)
    }
}