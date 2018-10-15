package de.jrk.mandelbrotset

object MandelbrotSet {
    fun testMandelbrot(c: Complex, iterations: Int): Int {
        var z = c
        var i = 0
        do {
            if (z.squaredAbsoluteValue > 4) return i
            z = z * z + c
        } while (++i < iterations)
        return -1
    }

    fun generateMandelbrotSet(set: Array<Array<Int>>, cX: Double, cY: Double, cWidth: Double, cHeight: Double, iterations: Int, x: Int = 0, y: Int = 0, width: Int = set.size, height: Int = set[0].size, stopWhen: () -> Boolean = { false }) {
        for (x in x until x + width) {
            for (y in y until y + height) {
                val c = Complex(
                        (x.toDouble() / set.size) * cWidth + cX,
                        (y.toDouble() / set[0].size) * cHeight + cY
                )
                set[x][y] = testMandelbrot(c, iterations)
                if (stopWhen()) {
                    return
                }
            }
        }
    }
}