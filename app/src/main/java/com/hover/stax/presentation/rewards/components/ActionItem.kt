package com.hover.stax.presentation.rewards.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hover.stax.R
import com.hover.stax.ui.theme.BrightBlue
import com.hover.stax.ui.theme.OffWhite
import com.hover.stax.ui.theme.StaxTheme

@Composable
fun ActionItem(points: Int, action: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(2.dp)
            .clickable { onClick() },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(BrightBlue)
                .size(50.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                modifier = Modifier
                    .widthIn(min = 30.dp)
                    .padding(dimensionResource(id = R.dimen.margin_5)),
                text = points.toString(),
                style = MaterialTheme.typography.h3,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = action,
            color = OffWhite,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.caption,
            modifier = Modifier
                .padding(top = dimensionResource(id = R.dimen.margin_10))
                .widthIn(min = 50.dp, max = 65.dp)
        )
    }
}

@Preview
@Composable
fun RewardItemPreview() {
    StaxTheme {
        Surface(color = MaterialTheme.colors.background) {
            ActionItem(points = 100, action = "Buy airtime") {}
        }
    }
}