package com.dailyexpense.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.dailyexpense.data.models.Duration
import com.dailyexpense.presentation.FilterViewModel
import com.dailyexpense.presentation.SortViewModel
import com.dailyexpense.presentation.TransactionListViewModel
import com.dailyexpense.ui.bottomsheet.BottomSheetHost
import com.dailyexpense.ui.bottomsheet.factory.rememberBottomSheetController
import com.dailyexpense.ui.bottomsheet.sheets.FilterBottomSheet
import com.dailyexpense.ui.bottomsheet.sheets.SortBottomSheet
import com.dailyexpense.ui.components.ErrorItem
import com.dailyexpense.ui.components.LoadingItem
import com.dailyexpense.ui.components.SearchBox
import com.dailyexpense.ui.components.TransactionRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionListScreen(
    viewModel: TransactionListViewModel = hiltViewModel(),
    filterViewModel: FilterViewModel = hiltViewModel(),
    sortViewModel: SortViewModel = hiltViewModel()
) {
    val filterState = filterViewModel.filterState
    val sortState = sortViewModel.sortState
    val filter = filterState.collectAsState().value

    val transactionPagingItems = remember {
        viewModel.getTransactionList(
            filterStateFlow = filterState,
            sortStateFlow = sortState
        )
    }.collectAsLazyPagingItems()

    val listState = rememberLazyListState()
    var searchQuery by remember { mutableStateOf(value = "") }

    val bottomSheetController = rememberBottomSheetController()

    BottomSheetHost(controller = bottomSheetController) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SearchBox(
                    query = searchQuery,
                    onQueryChanged = { query ->
                        searchQuery = query
                        viewModel.updateSearchQuery(newQuery = query)
                    },
                    onClearClicked = {
                        searchQuery = ""
                        viewModel.updateSearchQuery(newQuery = "")
                    },
                    onFilterClicked = {
                        bottomSheetController.show {
                            FilterBottomSheet(closeSheet = { bottomSheetController.hide() })
                        }
                    },
                    onSortClicked = {
                        bottomSheetController.show {
                            SortBottomSheet(closeSheet = { bottomSheetController.hide() })
                        }
                    },
                    isFilterApplied = filter.selectedDuration != Duration.Today ||
                        filter.selectedCategories.isNotEmpty() ||
                        filter.selectedTransactionTypes.isNotEmpty() ||
                        filter.selectedTransactionCategories.isNotEmpty() ||
                        filter.selectedTags.isNotEmpty()
                )
            }

            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(space = 8.dp),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 4.dp,
                    bottom = 80.dp
                )
            ) {
                items(transactionPagingItems.itemCount) { index ->
                    transactionPagingItems[index]?.let { transaction ->
                        TransactionRow(transactionEntity = transaction)
                    }
                }

                transactionPagingItems.apply {
                    when {
                        loadState.refresh is LoadState.Loading -> {
                            item { LoadingItem(text = "Loading transactions...") }
                        }

                        loadState.append is LoadState.Loading -> {
                            item { LoadingItem(text = "Loading more...") }
                        }

                        loadState.append is LoadState.Error -> {
                            val e = transactionPagingItems.loadState.append as LoadState.Error
                            item {
                                ErrorItem(
                                    message = e.error.message ?: "Error",
                                    onRetry = { retry() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
