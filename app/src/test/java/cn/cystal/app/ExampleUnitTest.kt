package cn.cystal.app

import cn.cystal.app.util.UtilsBigDecimal
import org.junit.Test
import java.math.BigDecimal

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val div = UtilsBigDecimal.div(1230189.0, 100.0)
        val formatToNumber = UtilsBigDecimal.formatToNumber(BigDecimal(div))
        println(div)
        println(formatToNumber)
    }
}
