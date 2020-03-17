package cn.cystal.app.base

import android.app.Application
import cn.cystal.app.BuildConfig
import cn.cystal.app.network.NetClient
import me.jessyan.autosize.AutoSizeConfig
import me.jessyan.autosize.unit.Subunits


/**
 *  author : zhangbao
 *  date : 2020-02-07 17:20
 *  description :
 */
class App : Application() {

    companion object {
        private var instance: App? = null
        fun getInstance(): App = instance!!
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        //对单位的自定义配置, 请在 App 启动时完成
        configUnits()
        //初始化设置网络库的配置
        NetClient.getInstance()
            .baseUrl(BASE_URL)
            .connectTimeOut(10)
            .writeTimeOut(60)
            .readTimeOut(60)
            .retryOnConnectionFailure(false)
            .init()
    }

    private fun configUnits() {
        //AndroidAutoSize 默认开启对 dp 的支持, 调用 UnitsManager.setSupportDP(false); 可以关闭对 dp 的支持
        //主单位 dp 和 副单位可以同时开启的原因是, 对于旧项目中已经使用了 dp 进行布局的页面的兼容
        //让开发者的旧项目可以渐进式的从 dp 切换到副单位, 即新页面用副单位进行布局, 然后抽时间逐渐的将旧页面的布局单位从 dp 改为副单位
        //最后将 dp 全部改为副单位后, 再使用 UnitsManager.setSupportDP(false); 将 dp 的支持关闭, 彻底隔离修改 density 所造成的不良影响
        //如果项目完全使用副单位, 则可以直接以像素为单位填写 AndroidManifest 中需要填写的设计图尺寸, 不需再把像素转化为 dp
        AutoSizeConfig.getInstance().unitsManager
            .setSupportDP(false).supportSubunits = Subunits.MM
    }
}