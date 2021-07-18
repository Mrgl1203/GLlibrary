package com.gl.gllibrary.widget.pullrefersh

/**
 * 分页刷新管理
 */
public class RefreshPageBean<T>(var page_load_size: Int = 20) {
    // page_load_size = 30//默认加载个数
    companion object {
        val SHOW_DATA_STATUS = 0//有数据状态

        @JvmField
        val SHOW_EMPTY_STATUS = 1//无数据空态

        @JvmField
        val SHOW_EMPTY_ERROR_STATUS = 2//数据错误空态
    }

    var first_page_index = 1//针对分页起始从0，1或者n开始，可按需设置
    var current_page_index = first_page_index//当前加载页面下标
    var isAllLoaded = false //数据是否加载完毕
    var emptyStatus = SHOW_DATA_STATUS //是否显示空页面及状态
    val dataList: MutableList<T> by lazy {//请求的数据集合
        mutableListOf()
    }

    //下拉刷新开始
    fun startRefresh() {
        current_page_index = first_page_index
        dataList.clear()
        isAllLoaded = false
        emptyStatus = SHOW_DATA_STATUS
    }

    //结束刷新
    fun endRefresh(refreshSuccess: Boolean, list: List<T>? = null) {
        loadFinish(refreshSuccess, list)
    }


    private fun loadFinish(refreshSuccess: Boolean, list: List<T>? = null) {
        if (refreshSuccess) {
            current_page_index++
            list?.let {
                val dataSize = list.size
                if (dataSize > 0) {
                    emptyStatus = SHOW_DATA_STATUS
                    dataList.addAll(it)
                } else {
                    emptyStatus = SHOW_EMPTY_STATUS
                }
                isAllLoaded = dataSize < page_load_size//返回个数小于请求个数，加载到底
            } ?: let {
                emptyStatus = SHOW_EMPTY_STATUS
            }
        } else {
            emptyStatus = SHOW_EMPTY_ERROR_STATUS
        }
    }

    //结束加载更多
    fun endLoadMore(loadMoreSuccess: Boolean, list: List<T>? = null) {
        loadFinish(loadMoreSuccess, list)
    }


}