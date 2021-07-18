package com.gl.gllibrary.widget.pullrefersh

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.LayoutRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.gl.gllibrary.R
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshFooter
import com.scwang.smart.refresh.layout.api.RefreshHeader
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import kotlinx.android.synthetic.main.gl_pullrefresh_recyclerview.view.*

/**
 * 分页下拉刷新recyclerView
 */
class PullRefreshRecyclerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), OnRefreshLoadMoreListener {
    interface OnPullRefreshLstener {

        /**
         * 请求下拉刷新
         */
        fun onRefresh()

        /**
         * 请求加载更多
         */
        fun onLoadMore()

        /**
         * 刷新数据
         * @param List：返回集合用于更新外部adapter
         * @param isAllLoaded：数据加载完成，可用于添加自己的footer或其他判断
         */
        fun onDataNotifyChanged(list: MutableList<Any>, isAllLoaded: Boolean)
    }

    var refreshListener: OnPullRefreshLstener? = null
    lateinit var mRefreshAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>
    private var mEmptyView: View? = null
    private var autoRefresh = true
    private var autoLoadMore = true
    val mRefreshPageBean: RefreshPageBean<Any> by lazy {
        RefreshPageBean<Any>()
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.gl_pullrefresh_recyclerview, this, true)
        val ta = context.obtainStyledAttributes(attrs, R.styleable.PullRefreshRecyclerView)
        autoRefresh = ta.getBoolean(R.styleable.PullRefreshRecyclerView_autoRefresh, true)
        autoLoadMore = ta.getBoolean(R.styleable.PullRefreshRecyclerView_autoLoadMore, true)
        if (autoRefresh) {
            autoRefresh(400)
        }
        if (autoLoadMore) {
            autoLoadMore()
        }
        val layoutManagerIndex = ta.getInt(R.styleable.PullRefreshRecyclerView_gl_layoutManager, 1)
        val layoutOritation =
            ta.getInt(R.styleable.PullRefreshRecyclerView_gl_layoutOritation, RecyclerView.VERTICAL)
        val spanCount = ta.getInt(R.styleable.PullRefreshRecyclerView_gl_spanCount, 1)
        val reverseLayout =
            ta.getBoolean(R.styleable.PullRefreshRecyclerView_gl_reverseLayout, false)
        ta.recycle()
        mSmartRefreshLayout.setOnRefreshLoadMoreListener(this)
        mSmartRefreshLayout.setRefreshHeader(ClassicsHeader(context))
        mSmartRefreshLayout.setRefreshFooter(ClassicsFooter(context))
        mRecyclerView.layoutManager = when (layoutManagerIndex) {
            1 -> LinearLayoutManager(context, layoutOritation, reverseLayout)
            2 -> GridLayoutManager(context, spanCount, layoutOritation, reverseLayout)
            else -> StaggeredGridLayoutManager(spanCount, layoutOritation)
        }
    }

    //使用此方法需要自己设置内容，自带的recyclerView将不起作用
    fun setRefreshContent(content: View, width: Int = 0, height: Int = 0) {
        mSmartRefreshLayout.setRefreshContent(content, width, height)
    }

    fun setRefreshHeader(header: RefreshHeader) {
        mSmartRefreshLayout.setRefreshHeader(header)
    }

    fun setRefreshFooter(footer: RefreshFooter) {
        mSmartRefreshLayout.setRefreshFooter(footer)
    }

    fun getRefreshLayout(): SmartRefreshLayout {
        return mSmartRefreshLayout
    }

    fun enableRefresh(enable: Boolean) {
        mSmartRefreshLayout.setEnableRefresh(enable)
    }

    fun enableLoadMore(enable: Boolean) {
        mSmartRefreshLayout.setEnableLoadMore(enable)
    }

    fun autoRefresh(delayTime: Int = 0) {
        mSmartRefreshLayout.autoRefresh(delayTime)
    }

    fun autoLoadMore(delayTime: Int = 0) {
        mSmartRefreshLayout.autoLoadMore(delayTime)
    }

    fun setAdapter(adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>) {
        mRefreshAdapter = adapter
        mRecyclerView.adapter = adapter
    }

    /**
     * 设置空态页面
     */
    fun setEmptyView(emptyView: View) {
        mEmptyView = emptyView
        layoutEmpty.addView(mEmptyView)
        mEmptyView?.visibility = View.GONE
    }

    fun setEmptyView(@LayoutRes res: Int) {
        setEmptyView(LayoutInflater.from(context).inflate(res, this, false))
    }

    fun setLayoutManager(layout: RecyclerView.LayoutManager) {
        mRecyclerView.layoutManager = layout
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        mRefreshPageBean.startRefresh()
        mSmartRefreshLayout.setEnableLoadMore(true)
        refreshListener?.onRefresh()
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        refreshListener?.onLoadMore()
    }

    /**
     * @parama success:请求是否成功
     * @param list：请求数据集合
     * @param showNoMoreData：是否显示 没有更多数据
     */
    fun finishRefresh(success: Boolean, list: List<Any>? = null, showNoMoreData: Boolean = false) {
        mRefreshPageBean.endRefresh(success, list)
        if (mRefreshPageBean.emptyStatus != RefreshPageBean.SHOW_DATA_STATUS) {
            mEmptyView?.visibility = View.VISIBLE
            mSmartRefreshLayout.visibility = View.GONE
            if (mEmptyView is PullRefreshEmptyView) {
                (mEmptyView as PullRefreshEmptyView).onEmptyStatus(mRefreshPageBean.emptyStatus)
            }
        } else {
            refreshListener?.onDataNotifyChanged(
                mRefreshPageBean.dataList,
                mRefreshPageBean.isAllLoaded
            )
            mEmptyView?.visibility = View.GONE
            mSmartRefreshLayout.visibility = VISIBLE
        }
        if (!showNoMoreData ) {//没有调用显示无数据，手动停止加载更多
            if(mRefreshPageBean.isAllLoaded){
                mSmartRefreshLayout.setEnableLoadMore(false)
            }
        }
        mSmartRefreshLayout.finishRefresh(
            0,
            success,
            if (showNoMoreData) mRefreshPageBean.isAllLoaded else false
        )

    }

    fun finishLoadMore(success: Boolean, list: List<Any>? = null, showNoMoreData: Boolean = false) {
        mRefreshPageBean.endLoadMore(success, list)
        if (mRefreshPageBean.emptyStatus == RefreshPageBean.SHOW_DATA_STATUS) {
            refreshListener?.onDataNotifyChanged(
                mRefreshPageBean.dataList,
                mRefreshPageBean.isAllLoaded
            )
        }
        if (!showNoMoreData ) {//没有调用显示无数据，手动停止加载更多
            if(mRefreshPageBean.isAllLoaded){
                mSmartRefreshLayout.setEnableLoadMore(false)
            }
        }
        mSmartRefreshLayout.finishLoadMore(
            0,
            success,
            if (showNoMoreData) mRefreshPageBean.isAllLoaded else false
        )
    }

    /**
     * @param isRefresh true ： 下拉刷新操作 false ：加载更多操作
     */
    fun finishLoadRequest(
        isRefresh: Boolean,
        success: Boolean,
        list: List<Any>? = null,
        showNoMoreData: Boolean = false
    ) {
        if (isRefresh) {
            finishRefresh(success, list, showNoMoreData)
        } else {
            finishLoadMore(success, list, showNoMoreData)
        }
    }

}