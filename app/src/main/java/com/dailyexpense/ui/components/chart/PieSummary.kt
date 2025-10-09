package com.dailyexpense.ui.components.chart

import android.graphics.Color
import android.graphics.Typeface
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.toColorInt
import com.dailyexpense.R
import com.dailyexpense.data.models.CategorySummary
import com.dailyexpense.ui.theme.LocalCustomColors
import com.dailyexpense.utils.extensions.toComposeColor
import com.dailyexpense.utils.extensions.toIndianCurrencyFormat
import com.dailyexpense.utils.rememberTypeface
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import java.util.Locale

@Composable
fun PieSummary(
    summary: List<CategorySummary>,
    title: String = "",
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit
) {
    Card(
        shape = RoundedCornerShape(size = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = LocalCustomColors.current.cardBg,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
    ) {
        Column(
            modifier = Modifier.padding(all = 12.dp),
        ) {
            if (title.isNotEmpty()) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                contentAlignment = Alignment.Center
            ) {
                val total = summary.sumOf { it.totalAmount }
                val centerText = if (summary.isNotEmpty())
                    stringResource(
                        id = R.string.label_rupee_sign_with_value,
                        total.toIndianCurrencyFormat()
                    ) else stringResource(id = R.string.no_data)
                val typeface = rememberTypeface(fontFamily = MaterialTheme.typography.bodyLarge.fontFamily)

                if (summary.isNotEmpty()) {
                    AndroidView(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        factory = { context ->
                            PieChart(context).apply {
                                description.isEnabled = false
                                isRotationEnabled = false
                                legend.isEnabled = false
                                setUsePercentValues(true)
                                setDrawEntryLabels(false)

                                setDrawCenterText(true)
                                setCenterTextSize(16f)
                                setCenterTextColor(Color.DKGRAY)

                                setOnChartValueSelectedListener(object :
                                    OnChartValueSelectedListener {
                                    override fun onValueSelected(e: Entry?, h: Highlight?) {
                                        onCategorySelected((e as? PieEntry)?.label)
                                    }

                                    override fun onNothingSelected() {
                                        onCategorySelected(null)
                                    }
                                })
                            }
                        },
                        update = { pieChart ->
                            updatePieChart(
                                pieChart = pieChart,
                                categorySummary = summary,
                                centerText = centerText,
                                centerTextTypeface = typeface
                            )
                        }
                    )
                } else {
                    Text(
                        text = "No chart data found",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }

            Spacer(modifier = Modifier.height(height = 12.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(space = 16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 4.dp)
            ) {
                summary.forEach { item ->
                    val boxSize by animateDpAsState(
                        targetValue = if (selectedCategory == item.categoryName) 20.dp else 16.dp,
                        animationSpec = tween(durationMillis = 300) // You can adjust the duration
                    )
                    val targetFontSize = if (selectedCategory == item.categoryName)
                        MaterialTheme.typography.bodyLarge.fontSize.value
                    else
                        MaterialTheme.typography.bodyMedium.fontSize.value

                    val fontSize by animateFloatAsState(
                        targetValue = targetFontSize,
                        animationSpec = tween(durationMillis = 300)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(size = boxSize)
                                .background(
                                    color = item.colorCode.toColorInt().toComposeColor(),
                                    CircleShape
                                )
                        )

                        Spacer(modifier = Modifier.width(width = 8.dp))

                        Text(
                            text = item.categoryName,
                            style = if (selectedCategory == item.categoryName) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(weight = 1f)
                        )

                        Text(
                            text = stringResource(
                                id = R.string.label_rupee_sign_with_value,
                                item.totalAmount.toIndianCurrencyFormat()
                            ),
                            fontSize = fontSize.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

private fun updatePieChart(
    pieChart: PieChart,
    categorySummary: List<CategorySummary>,
    centerText: String,
    centerTextTypeface: Typeface?
) {
    if (categorySummary.isNotEmpty()) {
        val entries = categorySummary.map {
            PieEntry(it.totalAmount.toFloat(), it.categoryName)
        }

        val pieDataSet = PieDataSet(entries, "").apply {
            colors = categorySummary.map { it.colorCode.toColorInt() }

            selectionShift = 12f
            sliceSpace = 2f

            valueTextSize = 14f
            valueTextColor = Color.WHITE

            yValuePosition = PieDataSet.ValuePosition.INSIDE_SLICE
            xValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE

            valueLinePart1Length = 0.3f
            valueLinePart2Length = 0.4f
            valueLineWidth = 1.5f
            valueLineColor = Color.DKGRAY
        }

        val pieData = PieData(pieDataSet).apply {
            setValueFormatter(object : ValueFormatter() {
                override fun getPieLabel(value: Float, pieEntry: PieEntry?): String {
                    return "${String.format(Locale.ENGLISH, "%.1f", value)}%"
                }
            })
        }
        pieChart.apply {
            this.centerText = "Total\n$centerText"
            this.setCenterTextTypeface(centerTextTypeface)
            this.data = pieData
            this.setUsePercentValues(true)
            this.setEntryLabelColor(Color.BLACK)
            this.setEntryLabelTextSize(12f)
            animateY(1000, Easing.EaseInOutQuad)
            invalidate()
        }
    } else {
        pieChart.clear()
        pieChart.centerText = centerText
    }
}