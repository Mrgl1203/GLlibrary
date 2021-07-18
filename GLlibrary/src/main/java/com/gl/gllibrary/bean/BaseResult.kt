package com.gl.gllibrary.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

public open class BaseResult : Serializable {
    @SerializedName("errorCode")
    private var code: Long = 0L
    @SerializedName("errorMsg")
    private var msg: String = ""

}