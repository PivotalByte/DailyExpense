package com.dailyexpense.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dailyexpense.R
import com.dailyexpense.ui.theme.LocalCustomColors


@Composable
fun SearchBox(
    modifier: Modifier = Modifier,
    query: String,
    onQueryChanged: (String) -> Unit,
    onClearClicked: () -> Unit,
    onFilterClicked: () -> Unit,
    onSortClicked: () -> Unit,
    isFilterApplied: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = modifier
                .weight(weight = 1f)
                .border(
                    width = 1.dp,
                    color = LocalCustomColors.current.searchBoxBorder,
                    shape = RoundedCornerShape(size = 12.dp)
                )
                .background(
                    color = LocalCustomColors.current.cardBg,
                    shape = RoundedCornerShape(size = 12.dp)
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Icon",
                tint = Color.Gray,
                modifier = Modifier.padding(start = 12.dp)
            )

            TextField(
                modifier = Modifier.weight(weight = 1f),
                value = query,
                onValueChange = onQueryChanged,
                placeholder = {
                    Text(
                        text = "Search transactions",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.Gray,
                            fontSize = 14.sp,
                        )
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    errorContainerColor = Color.Transparent,
                    focusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledTextColor = Color.Gray,
                    cursorColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                singleLine = true,
            )

            if (query.isNotEmpty()) {
                IconButton(onClick = onClearClicked) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear Search",
                        tint = Color.Gray
                    )
                }
            }
        }

        IconButton(
            onClick = onFilterClicked,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_filter),
                contentDescription = "Filter",
                tint = Color.Gray,
            )
            if (isFilterApplied) {
                Box(
                    modifier = Modifier
                        .size(size = 8.dp)
                        .offset(x = 8.dp, y = (-8).dp)
                        .background(
                            color = LocalCustomColors.current.primaryColor,
                            shape = RoundedCornerShape(percent = 50)
                        )
                )
            }
        }

        IconButton(
            onClick = onSortClicked,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_sort),
                contentDescription = "Sort",
                tint = Color.Gray,
            )
        }
    }
}

@Preview
@Composable
fun PreviewSearchBox() {
    var searchQuery by remember { mutableStateOf(value = "") }

    SearchBox(
        query = searchQuery,
        onQueryChanged = { searchQuery = it },
        onClearClicked = { searchQuery = "" },
        onFilterClicked = {  },
        onSortClicked = {  },
        isFilterApplied = true
    )
}