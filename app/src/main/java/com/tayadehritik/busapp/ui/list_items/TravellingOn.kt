package com.tayadehritik.busapp.ui.list_items

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tayadehritik.busapp.R

@Preview
@Composable
fun TravellingOn(
    routeShortName:String = "100",
    tripHeadSign:String = "Hinjawadi to MaNaPa",
    onCancel:() -> Unit = {}
) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        )
    ) {
        Row(
            modifier = Modifier.height(80.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(16.dp))
            Icon(
                modifier = Modifier.size(40.dp),
                painter = painterResource(id = R.drawable.directions_bus),
                contentDescription = routeShortName
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "Travelling on",
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$routeShortName - $tripHeadSign",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .clickable {
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Filled.Clear,
                    contentDescription = "Localized description",
                    tint = Color.White,
                    modifier = Modifier
                        .fillMaxHeight()
                        .background(
                            MaterialTheme.colorScheme.error,
                            RoundedCornerShape(0.dp)
                        )
                        .padding(24.dp, 0.dp)
                )

            }
            /*Spacer(modifier = Modifier.width(16.dp))
            IconButton(onClick = { *//*TODO*//* }) {
                Icon(Icons.Filled.Clear, contentDescription = "Localized description")
            }
            Spacer(modifier = Modifier.width(16.dp))
            */
        }
    }
}