package com.refresh.pos.ui.screen

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class SaleScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun emptyState_isShown() {
        composeTestRule.setContent {
            SaleScreen(onNavigateToSaleDetail = {})
        }

        composeTestRule.onNodeWithText("Clear").assertExists()
    }

    @Test
    fun endSaleButton_isPresent() {
        composeTestRule.setContent {
            SaleScreen(onNavigateToSaleDetail = {})
        }

        composeTestRule.onNodeWithText("End Sale").assertExists()
    }
}
