package com.etu.tuibagang.ui.apkdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.etu.tuibagang.ui.theme.TuiBaGangTheme

@Composable
internal fun PlaceholderScreen(title: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "$title tab (temporary empty)")
    }
}

@Preview(showBackground = true)
@Composable
private fun PlaceholderScreenPreview() {
    TuiBaGangTheme {
        PlaceholderScreen(title = "Products")
    }
}
