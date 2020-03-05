package cn.cystal.app.network

import cn.cystal.app.base.BaseModel
import cn.cystal.app.bean.*
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

/**
 * 所有接口
 */
interface IApiService {

    //获取验证码
    @POST("login/getCode")
    @Headers("Content-Type: application/json;charset=UTF-8")
    fun getVertifyCode(@Body body: RequestBody): Observable<VertifyCodeBean>

    //登陆
    @POST("login/userLogin")
    @Headers("Content-Type: application/json;charset=UTF-8")
    fun login(@Body body: RequestBody): Observable<String>

    //首页ICON展示
    @POST("/marketShelf/getBoutiqueSale")
    @Headers("Content-Type: application/json;charset=UTF-8")
    fun getBoutiqueSale(@Body body: RequestBody): Observable<BoutiqueSaleBean>

    //特权banner
    @POST("marketShelf/getBanner")
    @Headers("Content-Type: application/json;charset=UTF-8")
    fun getBanner(@Body body: RequestBody): Observable<BannerBean>

    //特权限时秒杀
    @POST("market/sale/list")
    @Headers("Content-Type: application/json;charset=UTF-8")
    fun getSale(@Body body: RequestBody): Observable<SaleBean>

    //查询余额
    @POST("market/getBalance")
    @Headers("Content-Type: application/json;charset=UTF-8")
    fun getBalanceInfo(@Body body: RequestBody): Observable<UserInfoBean>

    //订单列表
    @POST("market/orderList")
    @Headers("Content-Type: application/json;charset=UTF-8")
    fun getOrderList(@Body body: RequestBody): Observable<OrderBean>

    //提交凭证
    @POST("market/uploadVoucher")
    @Headers("Content-Type: application/json;charset=UTF-8")
    fun uploadVoucher(@Body body: RequestBody): Observable<BaseModel>

    //查看凭证
    @POST("manager/checkVoucher")
    @Headers("Content-Type: application/json;charset=UTF-8")
    fun checkVoucher(@Body body: RequestBody): Observable<CheckVoucherBean>

    //编辑凭证
    @POST("market/editVoucher")
    @Headers("Content-Type: application/json;charset=UTF-8")
    fun editVoucher(@Body body: RequestBody): Observable<BaseModel>

    //申请发票
    @POST("invoice/add")
    @Headers("Content-Type: application/json;charset=UTF-8")
    fun applyInvoice(@Body body: RequestBody): Observable<BaseModel>

    //消费明细
    @POST("market/balanceList")
    @Headers("Content-Type: application/json;charset=UTF-8")
    fun getBalanceList(@Body body: RequestBody): Observable<BalanceBean>

    //地址列表
    @POST("market/address/list")
    @Headers("Content-Type: application/json;charset=UTF-8")
    fun getAddressList(@Body body: RequestBody): Observable<MyAddressBean>

    //新增地址
    @POST("market/address/add")
    @Headers("Content-Type: application/json;charset=UTF-8")
    fun addAddress(@Body body: RequestBody): Observable<BaseModel>

    //编辑地址
    @POST("market/address/edit")
    @Headers("Content-Type: application/json;charset=UTF-8")
    fun editAddress(@Body body: RequestBody): Observable<BaseModel>

    //查询入会信息
    @POST("/market/user/info")
    @Headers("Content-Type: application/json;charset=UTF-8")
    fun getUserInfo(@Body body: RequestBody): Observable<UserBean>

    @Multipart
    @POST("/image/upload")
    fun uploadImg(
        @PartMap path: HashMap<String, RequestBody>,
        @Part map: List<MultipartBody.Part>
    ): Observable<UploadImgBean>

    @POST("/marketShelf/getOneSale")
    @Headers("Content-Type: application/json;charset=UTF-8")
    fun search(@Body body: RequestBody):Observable<BoutiqueSaleBean>

}
