package com.example.noteai.presentation.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.noteai.R
import com.example.noteai.utils.Constants.COPY
import com.example.noteai.utils.Constants.DESCRIPTION_CALENDAR_ICON
import com.example.noteai.utils.Constants.NOTE_SAVE
import com.example.noteai.utils.DateUtils

@Composable
fun NoteCard(
    title: String,
    onFavoriteClick: () -> Unit,
    onClick: () -> Unit,
    isFavorite: Boolean,
    createdAt: Long,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(83.dp)
            .border(1.dp, Color(0xFF38452D), RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.icon_copy),
                contentDescription = COPY,
                tint = Color.Black
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.calendar),
                        contentDescription = DESCRIPTION_CALENDAR_ICON,
                        modifier = Modifier.size(18.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = DateUtils.formatDate(createdAt),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
            IconButton(
                onClick = { onFavoriteClick() }
            ) {
                Icon(
                    painter = painterResource(if (isFavorite) R.drawable.icon_red else R.drawable.save),
                    contentDescription = NOTE_SAVE,
                    tint = if (isFavorite) Color(0xFFD22B2B) else Color.Black
                )
            }
        }
    }
}