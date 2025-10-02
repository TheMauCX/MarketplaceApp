package com.marketplace.app.domain.util
import androidx.room.TypeConverter
import java.math.BigDecimal

class BigDecimalConverter {

    // Convierte el BigDecimal a String para guardarlo en la base de datos
    @TypeConverter
    fun fromBigDecimal(value: BigDecimal?): String? {
        return value?.toPlainString()
    }

    // Convierte el String de la base de datos de vuelta a BigDecimal
    @TypeConverter
    fun toBigDecimal(value: String?): BigDecimal? {
        // Usa `let` para devolver null si el valor es nulo o vacío
        return value?.let {
            if (it.isEmpty()) null else BigDecimal(it)
        }
    }
}