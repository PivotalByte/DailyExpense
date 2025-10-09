package com.dailyexpense.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dailyexpense.R
import com.dailyexpense.ui.theme.LocalCustomColors
import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec
import com.skydoves.balloon.compose.Balloon
import com.skydoves.balloon.compose.rememberBalloonBuilder
import com.skydoves.balloon.compose.setBackgroundColor

@Composable
fun AmountCard(
    modifier: Modifier,
    shape: RoundedCornerShape,
    colors: CardColors,
    elevation: CardElevation,
    cardTitle: String,
    cardAmount: String,
    cardTitleColor: Color,
    tooltipMsg: String = "",
) {
    Card(
        modifier = modifier,
        shape = shape,
        colors = colors,
        elevation = elevation
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = LocalCustomColors.current.dashboardCardTitleBg)
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp, bottom = 2.dp),
                    textAlign = TextAlign.Center,
                    text = cardTitle,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = cardTitleColor
                )
                if (tooltipMsg.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .padding(end = 5.dp)
                            .align(Alignment.CenterEnd),
                    ) {

                        val builder = rememberBalloonBuilder {
                            setArrowSize(10)
                            setArrowPosition(0.5f)
                            setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
                            setWidth(BalloonSizeSpec.WRAP)
                            setHeight(BalloonSizeSpec.WRAP)
                            setMaxWidth(250)
                            setText(tooltipMsg)
                            setPadding(12)
                            setMarginHorizontal(12)
                            setCornerRadius(8f)
                            setEnableAutoSized(true)
                            setBackgroundColor(Color.Gray)
                            setBalloonAnimation(BalloonAnimation.ELASTIC)
                        }

                        Balloon(
                            modifier = Modifier.align(Alignment.Center),
                            builder = builder,
                        ) { balloonWindow ->
                            Box {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_info),
                                    contentDescription = "Info",
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clickable {
                                            balloonWindow.showAlignBottom()
                                        },
                                    colorFilter = ColorFilter.tint(LocalCustomColors.current.primaryColor),
                                )
                            }
                        }
                    }
                }
            }
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 10.dp),
                textAlign = TextAlign.Center,
                text = cardAmount,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Normal
                ),
                color = LocalCustomColors.current.primaryColor
            )
        }
    }
}

@Preview
@Composable
fun PreviewDashboardAmountCard() {
    AmountCard(
        modifier = Modifier,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = LocalCustomColors.current.cardBg,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        cardTitle = stringResource(id = R.string.label_total_balance),
        cardAmount = stringResource(id = R.string.label_rupee_sign_with_value, "10,000"),
        cardTitleColor = MaterialTheme.colorScheme.onSurfaceVariant,
        tooltipMsg = stringResource(id = R.string.tooltip_total_balance)
    )
}