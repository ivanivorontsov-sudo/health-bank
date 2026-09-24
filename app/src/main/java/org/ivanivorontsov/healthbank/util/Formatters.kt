package org.ivanivorontsov.healthbank.util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val ru = Locale("ru", "RU")
private val moneyFmt = NumberFormat.getNumberInstance(ru)
private val dateFmt = SimpleDateFormat("dd MMM yyyy", ru)
private val dateTimeFmt = SimpleDateFormat("dd.MM.yyyy HH:mm", ru)

fun formatZdr(amount: Long): String = "${moneyFmt.format(amount)} ЗДР"

fun formatDate(ms: Long): String = dateFmt.format(Date(ms))

fun formatDateTime(ms: Long): String = dateTimeFmt.format(Date(ms))

fun statusRu(status: String): String = when (status) {
    "COMPLETED" -> "Исполнено"
    "PENDING" -> "В ожидании"
    "FAILED" -> "Ошибка"
    else -> status
}

fun typeRu(type: String): String = when (type) {
    "CREDIT" -> "Зачисление"
    "DEBIT" -> "Списание"
    "TRANSFER" -> "Перевод"
    else -> type
}
