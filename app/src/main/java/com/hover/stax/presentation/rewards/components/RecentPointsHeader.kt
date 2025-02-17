package com.hover.stax.presentation.rewards.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import com.hover.stax.R
import com.hover.stax.ui.theme.StaxTheme

@Composable
fun RecentPointsHeader(onClickRefresh: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(id = R.dimen.margin_16),
                vertical = dimensionResource(id = R.dimen.margin_10)
            )
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = stringResource(id = R.string.recent_points_earned),
            style = MaterialTheme.typography.button.copy(
                textDecoration = TextDecoration.Underline
            )
        )

        Text(
            modifier = Modifier
                .wrapContentWidth()
                .clickable { onClickRefresh() },
            text = stringResource(id = R.string.refresh),
            style = MaterialTheme.typography.button.copy(
                textDecoration = TextDecoration.Underline
            )
        )
    }
}

@Preview
@Composable
fun RecentPointsHeaderPreview() {
    StaxTheme {
        Surface(modifier = Modifier.wrapContentSize(), color = MaterialTheme.colors.background) {
            RecentPointsHeader {}
        }
    }
}