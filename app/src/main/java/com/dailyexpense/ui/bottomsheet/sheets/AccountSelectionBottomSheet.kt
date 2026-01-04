package com.dailyexpense.ui.bottomsheet.sheets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.dailyexpense.data.room.entity.AccountEntity
import com.dailyexpense.ui.theme.LocalCustomColors

@Composable
fun AccountSelectionBottomSheet(
    accounts: List<AccountEntity>,
    selectedAccount: AccountEntity?,
    onAccountSelected: (AccountEntity) -> Unit,
    closeSheet: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Select Account",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        if (accounts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No accounts available",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                accounts.forEach { account ->
                    AccountItem(
                        account = account,
                        isSelected = selectedAccount?.accountId == account.accountId,
                        onClick = {
                            onAccountSelected(account)
                            closeSheet()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AccountItem(
    account: AccountEntity,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) {
                    LocalCustomColors.current.primaryColor
                } else {
                    LocalCustomColors.current.searchBoxBorder
                },
                shape = RoundedCornerShape(12.dp)
            )
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) {
                    LocalCustomColors.current.primaryColor.copy(alpha = 0.1f)
                } else {
                    LocalCustomColors.current.cardBg
                }
            )
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = account.accountBankName,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = account.accountHolderName,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = "₹${String.format(java.util.Locale.US, "%.2f", account.balance)}",
            style = MaterialTheme.typography.titleSmall,
            color = if (isSelected) {
                LocalCustomColors.current.primaryColor
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )
    }
}
