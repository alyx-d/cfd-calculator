package calculator.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat

@Composable
fun Calculator() {
    var num = remember { mutableStateOf(BigDecimal.ZERO) }
    val format = DecimalFormat("#,###.###############")
    val text = remember { mutableStateOf(format.format(num.value)) }
    val result = remember { mutableStateOf("") }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        DisplayNum(text, result)
        ButtonGrid(num, text, result)
    }
}

@Composable
fun DisplayNum(text: MutableState<String>, result: MutableState<String>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalAlignment = Alignment.End
    ) {
        Text(
            text = result.value,
        )
        Text(
            text = text.value,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun ButtonGrid(
    num: MutableState<BigDecimal>,
    text: MutableState<String>,
    result: MutableState<String>,
) {
    val buttons = listOf(
        "CE", "C", "<-", "/",
        "7", "8", "9", "X",
        "4", "5", "6", "-",
        "1", "2", "3", "+",
        "-/+", "0", ".", "=",
    )
    val pointClicked = remember { mutableStateOf(false) }
    val equalClicked = remember { mutableStateOf(false) }
    val operatorNum = remember { mutableStateOf(BigDecimal.ONE) }
    val operator = remember { mutableStateOf("") }
    val operatorClicked = remember { mutableStateOf(false) }
    val subOrPlusClicked = remember { mutableStateOf(false) }
    fun onEqualClicked() {
        equalClicked.value = true
        when (operator.value) {
            "" -> result.value = text.value + " = "
            "+" -> {
                result.value =
                    operatorNum.value.toPlainString() + " ${operator.value} " + num.value.toPlainString() + " = "
                operatorNum.value = operatorNum.value.plus(num.value)
                text.value = operatorNum.value.toPlainString()
            }

            "-" -> {
                result.value =
                    operatorNum.value.toPlainString() + " ${operator.value} " + num.value.toPlainString() + " = "
                operatorNum.value = operatorNum.value.subtract(num.value)
                text.value = operatorNum.value.toPlainString()
            }

            "X" -> {
                result.value =
                    operatorNum.value.toPlainString() + " ${operator.value} " + num.value.toPlainString() + " = "
                operatorNum.value = operatorNum.value.multiply(num.value)
                text.value = operatorNum.value.toPlainString()
            }

            "/" -> {
                result.value =
                    operatorNum.value.toPlainString() + " ${operator.value} " + num.value.toPlainString() + " = "
                if (num.value != BigDecimal.ZERO) {
                    operatorNum.value = operatorNum.value.divide(num.value, 15, RoundingMode.HALF_UP)
                    text.value = operatorNum.value.toPlainString()
                }else {
                    text.value = "除数不能为零"
                }
            }
        }
    }

    fun onNumberClicked(numValue: Int) {
        when {
            equalClicked.value -> {
                num.value = BigDecimal("$numValue")
                if (subOrPlusClicked.value.not()) {
                    result.value = ""
                }
                equalClicked.value = false
                subOrPlusClicked.value = false
            }

            pointClicked.value -> {
                val str = num.value.toPlainString()
                num.value = BigDecimal("$str.$numValue")
                pointClicked.value = false
            }

            operatorClicked.value -> {
                num.value = BigDecimal("$numValue")
                operatorClicked.value = false
            }

            else -> {
                val scale = num.value.scale()
                if (scale > 0) {
                    num.value = BigDecimal("${num.value.toPlainString()}$numValue")
                } else {
                    num.value = num.value.multiply(BigDecimal("10")).plus(BigDecimal("$numValue"))
                }
            }
        }
        text.value = num.value.toPlainString()
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        contentPadding = PaddingValues(5.dp),
    ) {
        items(buttons) { button ->
            Box(
                modifier = Modifier
                    .padding(3.dp)
                    .clip(RoundedCornerShape(7.dp))
                    .background(MaterialTheme.colors.secondaryVariant)
                    .widthIn(min = 50.dp, max = 80.dp)
                    .heightIn(min = 50.dp, max = 80.dp)
                    .clickable {
                        val numValue = button.toIntOrNull()
                        when {
                            numValue != null -> onNumberClicked(numValue)
                            button == "." -> {
                                if (pointClicked.value.not()) {
                                    pointClicked.value = true
                                    text.value += button
                                }
                            }

                            button == "-/+" -> {
                                num.value = BigDecimal.ZERO - num.value
                                text.value = num.value.toPlainString()
                                subOrPlusClicked.value = true
                            }

                            button == "=" -> onEqualClicked()
                            listOf("+", "-", "X", "/").contains(button) -> {
                                if (equalClicked.value.not()) {
                                    operatorNum.value = num.value
                                }
                                operator.value = button
                                operatorClicked.value = true
                                result.value = num.value.toPlainString() + " $button "
                            }

                            button == "C" -> {
                                num.value = BigDecimal.ZERO
                                operatorNum.value = num.value
                                operator.value = ""
                                text.value = "0"
                                result.value = ""
                            }

                            button == "CE" -> {
                                num.value = BigDecimal.ZERO
                                operator.value = ""
                                text.value = "0"
                                result.value = ""
                            }

                            button == "<-" -> {
                                text.value =
                                    if (text.value.length > 1) text.value.substring(0, text.value.length - 1) else "0"
                                num.value = BigDecimal(text.value)
                            }
                        }
                    }
            ) {
                Text(
                    modifier = Modifier
                        .align(Alignment.Center),
                    text = button, fontSize = 20.sp,
                    color = MaterialTheme.colors.onPrimary,
                )
            }
        }
    }


}