package cn.cystal.app.util

import java.math.BigDecimal
import java.text.DecimalFormat

/**
 *  author : zhangbao
 *  date : 2020-02-14 20:42
 *  description :
 */
object UtilsBigDecimal {

    // 需要精确至小数点后几位
    const val DECIMAL_POINT_NUMBER:Int = 2

    // 加法运算
    @JvmStatic
    fun add(d1:Double,d2:Double):Double = BigDecimal(d1).add(BigDecimal(d2)).setScale(DECIMAL_POINT_NUMBER,BigDecimal.ROUND_DOWN).toDouble()

    @JvmStatic
    fun add(d1:Double,d2:Double,accuracy:Int):Double = BigDecimal(d1).add(BigDecimal(d2)).setScale(accuracy,BigDecimal.ROUND_DOWN).toDouble()

    // 减法运算
    @JvmStatic
    fun sub(d1:Double,d2: Double):Double = BigDecimal(d1).subtract(BigDecimal(d2)).setScale(DECIMAL_POINT_NUMBER,BigDecimal.ROUND_DOWN).toDouble()
    @JvmStatic
    fun sub(d1:Double,d2: Double,accuracy:Int):Double = BigDecimal(d1).subtract(BigDecimal(d2)).setScale(accuracy,BigDecimal.ROUND_DOWN).toDouble()

    // 乘法运算
    @JvmStatic
    fun mul(d1:Double,d2: Double):Double = BigDecimal(d1).multiply(BigDecimal(d2)).setScale(DECIMAL_POINT_NUMBER,BigDecimal.ROUND_DOWN).toDouble()
    @JvmStatic
    fun mul(d1:Double,d2: Double,accuracy:Int):Double = BigDecimal(d1).multiply(BigDecimal(d2)).setScale(accuracy,BigDecimal.ROUND_DOWN).toDouble()

    // 除法运算
    @JvmStatic
    fun div(d1:Double,d2: Double):Double = BigDecimal(d1).divide(BigDecimal(d2)).setScale(DECIMAL_POINT_NUMBER,BigDecimal.ROUND_DOWN).toDouble()
    @JvmStatic
    fun div(d1:Double,d2: Double,accuracy:Int):Double = BigDecimal(d1).divide(BigDecimal(d2)).setScale(accuracy,BigDecimal.ROUND_DOWN).toDouble()

    @JvmStatic
    fun formatToNumber(obj: BigDecimal): String? {
        val df = DecimalFormat("#.00")
        return if (obj.compareTo(BigDecimal.ZERO) == 0) {
            "0.00"
        } else if (obj.compareTo(BigDecimal.ZERO) > 0 && obj.compareTo(BigDecimal(1)) < 0) {
            "0" + df.format(obj).toString()
        } else {
            df.format(obj).toString()
        }
    }
}