package com.example.android.clock

enum class TimeZones(val zone: Int) {
    UTC_M12(-12), UTC_M11(-11), UTC_M10(-10), UTC_M9(-9),
    UTC_M8(-8), UTC_M7(-7), UTC_M6(-6), UTC_M5(-5),
    UTC_M4(-4), UTC_M3(-3), UTC_M2(-2), UTC_M1(-1),
    UTC_P0(0), UTC_P1(1), UTC_P2(2), UTC_P3(3),
    UTC_P4(4), UTC_P5(5), UTC_P6(6), UTC_P7(7),
    UTC_P8(8), UTC_P9(9), UTC_P10(10), UTC_P11(11),
    UTC_P12(12), UTC_P13(13), UTC_P14(14);


    companion object{
        val timeZonesList = values()
        fun getTimeZoneByOffset(offset: Int): TimeZones{
            try {
                return timeZonesList[offset + 12]
            }
            catch (e: ArrayIndexOutOfBoundsException){
                return UTC_P0
            }
        }
    }
}