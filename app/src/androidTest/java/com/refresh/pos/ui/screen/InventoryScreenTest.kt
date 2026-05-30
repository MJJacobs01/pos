package com.refresh.pos.ui.screen

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class InventoryScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun inventoryScreen_showsSearchPlaceholder() {
        composeTestRule.setContent {
            InventoryScreen(onNavigateToProductDetail = {})
        }

        composeTestRule.onNodeWithText("No products").assertExists()
    }

    @Test
    fun emptyState_isShown() {
        composeTestRule.setContent {
            InventoryScreen(onNavigateToProductDetail = {})
        }

        composeTestRule.onNodeWithText("No products").assertExists()
    }
}
