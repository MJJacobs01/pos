package com.refresh.pos.ui.screen

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowWidthSizeClass
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import com.refresh.pos.R
import com.refresh.pos.ui.LocaleHelper
import com.refresh.pos.ui.viewmodel.InventoryViewModel
import com.refresh.pos.ui.viewmodel.SaleViewModel
import androidx.compose.ui.tooling.preview.Preview

private data class Tab(
    val titleRes: Int,
    val icon: ImageVector
)

private val tabs = listOf(
    Tab(R.string.tab_inventory, Icons.Default.Inventory2),
    Tab(R.string.tab_sale, Icons.Default.PointOfSale),
    Tab(R.string.tab_report, Icons.Default.BarChart)
)

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onNavigateToProductDetail: (Long) -> Unit,
    onNavigateToSaleDetail: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var showOverflowMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val activity = context as? Activity
    val scope = rememberCoroutineScope()
    val saleViewModel: SaleViewModel = hiltViewModel()
    val inventoryViewModel: InventoryViewModel = hiltViewModel()
    val inventoryState by inventoryViewModel.uiState.collectAsStateWithLifecycle()

    val scanLauncher = rememberLauncherForActivityResult(ScanContract()) { result ->
        result.contents?.let { barcode -> inventoryViewModel.onBarcodeScanned(barcode) }
    }
    fun launchScanner() {
        scanLauncher.launch(ScanOptions().apply {
            setDesiredBarcodeFormats(ScanOptions.ALL_CODE_TYPES)
            setPrompt("Scan a barcode")
            setBeepEnabled(false)
            setBarcodeImageEnabled(false)
        })
    }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> if (granted) launchScanner() }
    fun startScan() {
        when {
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED -> launchScanner()
            else -> cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    val adaptiveInfo = currentWindowAdaptiveInfo()
    val widthSizeClass = adaptiveInfo.windowSizeClass.windowWidthSizeClass

    fun changeLanguage(localeCode: String) {
        LocaleHelper.setLocale(context, localeCode)
        activity?.recreate()
    }

    fun onInventoryProductTapped(productId: Long, unitPrice: Double) {
        saleViewModel.addItem(productId, 1)
        selectedTab = 1
    }

    val topBar = @Composable {
        if (selectedTab == 0) {
            Column {
                TopAppBar(
                    title = { Text(stringResource(tabs[selectedTab].titleRes)) },
                    actions = {
                        IconButton(onClick = { showOverflowMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                        }
                        DropdownMenu(
                            expanded = showOverflowMenu,
                            onDismissRequest = { showOverflowMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("English") },
                                onClick = { showOverflowMenu = false; changeLanguage("en") }
                            )
                            DropdownMenuItem(
                                text = { Text("ไทย") },
                                onClick = { showOverflowMenu = false; changeLanguage("th") }
                            )
                            DropdownMenuItem(
                                text = { Text("日本語") },
                                onClick = { showOverflowMenu = false; changeLanguage("jp") }
                            )
                        }
                    }
                )
                SearchBarDefaults.InputField(
                    query = inventoryState.searchQuery,
                    onQueryChange = { inventoryViewModel.onSearchQueryChange(it) },
                    onSearch = { inventoryViewModel.onSearchQueryChange(it) },
                    expanded = false,
                    onExpandedChange = {},
                    placeholder = { Text(stringResource(R.string.hint_search)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    trailingIcon = {
                        IconButton(onClick = { startScan() }) {
                            Icon(Icons.Default.QrCodeScanner, contentDescription = "Scan")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        } else {
            TopAppBar(
                title = { Text(stringResource(tabs[selectedTab].titleRes)) },
                actions = {
                    IconButton(onClick = { showOverflowMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                    }
                    DropdownMenu(
                        expanded = showOverflowMenu,
                        onDismissRequest = { showOverflowMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("English") },
                            onClick = { showOverflowMenu = false; changeLanguage("en") }
                        )
                        DropdownMenuItem(
                            text = { Text("ไทย") },
                            onClick = { showOverflowMenu = false; changeLanguage("th") }
                        )
                        DropdownMenuItem(
                            text = { Text("日本語") },
                            onClick = { showOverflowMenu = false; changeLanguage("jp") }
                        )
                    }
                }
            )
        }
    }

    val content = @Composable { innerPadding: androidx.compose.foundation.layout.PaddingValues ->
        when (selectedTab) {
            0 -> InventoryScreen(
                onNavigateToProductDetail = onNavigateToProductDetail,
                onProductTapped = ::onInventoryProductTapped,
                modifier = Modifier.padding(innerPadding)
            )
            1 -> SaleScreen(
                onNavigateToSaleDetail = onNavigateToSaleDetail,
                modifier = Modifier.padding(innerPadding)
            )
            2 -> ReportScreen(
                onNavigateToSaleDetail = onNavigateToSaleDetail,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }

    when (widthSizeClass) {
        WindowWidthSizeClass.EXPANDED -> {
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Open)
            ModalNavigationDrawer(
                drawerState = drawerState,
                gesturesEnabled = false,
                drawerContent = {
                    ModalDrawerSheet(modifier = Modifier.width(280.dp)) {
                        Text(
                            text = stringResource(R.string.app_name),
                            style = androidx.compose.material3.MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(16.dp)
                        )
                        tabs.forEachIndexed { index, tab ->
                            NavigationDrawerItem(
                                icon = { Icon(tab.icon, contentDescription = null) },
                                label = { Text(stringResource(tab.titleRes)) },
                                selected = selectedTab == index,
                                onClick = { selectedTab = index }
                            )
                        }
                    }
                }
            ) {
                Scaffold(topBar = topBar) { innerPadding ->
                    Row(modifier = Modifier.padding(innerPadding)) {
                        content(androidx.compose.foundation.layout.PaddingValues(0.dp))
                    }
                }
            }
        }
        WindowWidthSizeClass.MEDIUM -> {
            Row(modifier = modifier) {
                NavigationRail(modifier = Modifier.fillMaxHeight()) {
                    tabs.forEachIndexed { index, tab ->
                        NavigationRailItem(
                            icon = { Icon(tab.icon, contentDescription = null) },
                            label = { Text(stringResource(tab.titleRes)) },
                            selected = selectedTab == index,
                            onClick = { selectedTab = index }
                        )
                    }
                }
                Scaffold(topBar = topBar) { innerPadding ->
                    content(innerPadding)
                }
            }
        }
        else -> {
            Scaffold(
                topBar = topBar,
                bottomBar = {
                    NavigationBar {
                        tabs.forEachIndexed { index, tab ->
                            NavigationBarItem(
                                icon = { Icon(tab.icon, contentDescription = stringResource(tab.titleRes)) },
                                label = { Text(stringResource(tab.titleRes)) },
                                selected = selectedTab == index,
                                onClick = { selectedTab = index }
                            )
                        }
                    }
                }
            ) { innerPadding ->
                content(innerPadding)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showSystemUi = true)
@Composable
private fun MainScreenPreview() {
    com.refresh.pos.ui.theme.PosTheme {
        Scaffold(
            topBar = {
                TopAppBar(title = { Text("Inventory") })
            },
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = true,
                        onClick = { },
                        icon = {
                            Icon(
                                Icons.Default.Inventory2,
                                contentDescription = "Inventory"
                            )
                        },
                        label = { Text("Inventory") }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = { },
                        icon = {
                            Icon(
                                Icons.Default.PointOfSale,
                                contentDescription = "Sale"
                            )
                        },
                        label = { Text("Sale") }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = { },
                        icon = {
                            Icon(
                                Icons.Default.BarChart,
                                contentDescription = "Report"
                            )
                        },
                        label = { Text("Report") }
                    )
                }
            }
        ) { innerPadding ->
            Text(
                text = "Inventory Tab",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}
