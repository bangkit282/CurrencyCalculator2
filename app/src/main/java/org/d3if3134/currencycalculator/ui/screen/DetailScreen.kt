package org.d3if3134.currencycalculator.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.d3if3134.currencycalculator.R
import org.d3if3134.currencycalculator.database.CurrencyDb
import org.d3if3134.currencycalculator.model.CurrencyCode
import org.d3if3134.currencycalculator.navigation.Screen
import org.d3if3134.currencycalculator.utils.SettingDataStore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(navController : NavHostController) {
    val dataStore = SettingDataStore(LocalContext.current)
    val isList by dataStore.layoutFLow.collectAsState(true)
    Scaffold (
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(id = R.string.back),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                title = { Text(text = stringResource(id = R.string.detail_screen)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                actions=  {
                    if (isList) {
                        IconButton(onClick = { CoroutineScope(Dispatchers.IO).launch { dataStore.saveLayout(false) } }) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_grid_view_24),
                                contentDescription = stringResource(id = R.string.list),
                                tint = contentColorFor(MaterialTheme.colorScheme.primaryContainer)
                            )
                        }
                    } else {
                        IconButton(onClick = { CoroutineScope(Dispatchers.IO).launch { dataStore.saveLayout(true) } }) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_view_list_24),
                                contentDescription = stringResource(id = R.string.list),
                                tint = contentColorFor(MaterialTheme.colorScheme.primaryContainer)
                            )
                        }
                    }
                }

            )
        }
    ) { innerPadding ->
        DetailContent(modifier = Modifier.padding(innerPadding), navController, isList)
    }
}

@Composable
fun DetailContent(modifier: Modifier = Modifier, navController: NavHostController, isList: Boolean) {
    val context = LocalContext.current
    val db = CurrencyDb.getInstance(context)
    val factory = ViewModelFactory(db.dao)
    val viewModel: MainViewModel = viewModel(factory = factory)
    val data = viewModel.data.collectAsState().value

    if (isList) {
        LazyColumn(modifier = modifier, contentPadding = PaddingValues(16.dp))
        {
            items(
                items = data,
                itemContent = { currency ->
                    ListItemDetail(currency = currency) {
                        navController.navigate(Screen.UpdateCurrency.withId(currency.id))
                    }
                }
            )
        }
    } else {
        LazyVerticalStaggeredGrid(
            modifier = modifier.fillMaxWidth(),
            columns = StaggeredGridCells.Fixed(2),
            verticalItemSpacing = 8.dp,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(8.dp, 8.dp, 8.dp, 16.dp)

        ) {
            items(data) {
                GridItemDetail(currency = it) {
                    navController.navigate(Screen.UpdateCurrency.withId(it.id))
                }

            }
        }
    }
}

@Composable
fun GridItemDetail(currency: CurrencyCode, onClick: () -> Unit) {
    Card (
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primaryContainer)
    ){
        Image(
            painter = painterResource(id = currency.flag),
            contentDescription = null,
            modifier = Modifier
                .padding(16.dp)
        )
        Text(
            text = currency.currency,
            modifier = Modifier.padding(16.dp),
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(text = "1 USD = ${currency.rate} ${currency.code}", modifier = Modifier.padding(16.dp))

    }
}

