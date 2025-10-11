package com.dailyexpense.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dailyexpense.R
import com.dailyexpense.data.enums.TransactionCategory
import com.dailyexpense.data.enums.TransactionType
import com.dailyexpense.data.enums.getTransactionCategoryAsString
import com.dailyexpense.data.room.entity.TransactionEntity
import com.dailyexpense.ui.theme.ExpenseColor
import com.dailyexpense.ui.theme.IncomeColor
import com.dailyexpense.ui.theme.LocalCustomColors
import com.dailyexpense.ui.theme.NeutralColor
import com.dailyexpense.utils.DateUtil.toOrdinalAnnotatedDate
import java.util.Date

@Composable
fun TransactionRow(transactionEntity: TransactionEntity) {
    val (iconRes, bgColor) = when (transactionEntity.transactionType) {
        TransactionType.INCOME -> {
            R.drawable.ic_income to IncomeColor.copy(alpha = 0.25f)
        }

        TransactionType.EXPENSE -> {
            R.drawable.ic_expense to ExpenseColor.copy(alpha = 0.25f)
        }

        else -> {
            R.drawable.ic_neutral to NeutralColor.copy(alpha = 0.25f)
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = LocalCustomColors.current.cardBg,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = bgColor,
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = "App Icon",
                    modifier = Modifier.size(28.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 10.dp)
            ) {
                Text(
                    text = transactionEntity.title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = transactionEntity.transactionCategory.getTransactionCategoryAsString(
                        LocalContext.current
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = when (transactionEntity.transactionType) {
                        TransactionType.EXPENSE -> {
                            stringResource(R.string.rupee_sign_expense, transactionEntity.amount)
                        }

                        TransactionType.INCOME -> {
                            stringResource(R.string.rupee_sign_income, transactionEntity.amount)
                        }

                        else -> {
                            stringResource(R.string.rupee_sign_neutral, transactionEntity.amount)
                        }
                    },
                    style = MaterialTheme.typography.titleMedium,
                    color = when (transactionEntity.transactionType) {
                        TransactionType.INCOME -> {
                            IncomeColor
                        }

                        TransactionType.EXPENSE -> {
                            ExpenseColor
                        }

                        else -> {
                            NeutralColor
                        }
                    }
                )
                Text(
                    text = transactionEntity.date.toOrdinalAnnotatedDate(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview()
@Composable
fun TransactionRowPreview() {
    val transactionEntity = TransactionEntity(
        title = "abcd",
        categoryId = 1,
        accountId = 1,
        date = Date(),
        transactionType = TransactionType.EXPENSE,
        transactionCategory = TransactionCategory.UPI,
        amount = 100.0
    )
    TransactionRow(transactionEntity)
}
