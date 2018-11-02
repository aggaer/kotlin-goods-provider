package service

import kotlinx.coroutines.*
import org.junit.Test
import kotlin.system.measureTimeMillis

suspend fun calA(): Int {
    delay(1000)
    return 3
}

suspend fun calB(): Int {
    delay(5000)
    return 2
}


class SuspendTest {
    @Test
    fun test() = runBlocking {
        val time = measureTimeMillis {
            val tarA = async { calA() }
            val carB = async { calB() }
            println("result is ${tarA.await()}")
            println("result is ${carB.await()}")
        }
        println("complete in $time ms")
    }

    @Test
    fun suspendTest() {
        runBlocking {
            val launch = GlobalScope.launch {
                testException()
            }
            launch.join()
            println("finish")
        }

    }

    private suspend fun testException() {
        delay(1000)
        throw RuntimeException("测试异常")
    }

    @Test
    fun testGlobal() {
        measureTimeMillis {
            fun first() = GlobalScope.launch {
                delay(1)
                1
            }
        }
    }
}

