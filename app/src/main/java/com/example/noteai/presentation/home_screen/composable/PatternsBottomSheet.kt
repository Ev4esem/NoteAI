package com.example.noteai.presentation.home_screen.composable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.noteai.R
import com.example.noteai.presentation.home_screen.HomeIntent
import com.example.noteai.presentation.home_screen.HomeIntent.ChoosePattern
import com.example.noteai.presentation.home_screen.HomeIntent.DismissPatternsBottomSheet
import com.example.noteai.presentation.home_screen.PatternState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatternsBottomSheet(
    currentPatternState: PatternState,
    onIntent: (HomeIntent) -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = {
            onIntent(DismissPatternsBottomSheet)
        },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false),
        shape = RoundedCornerShape(
            topStart = 32.dp,
            topEnd = 32.dp,
            bottomEnd = 0.dp,
            bottomStart = 0.dp
        ),
        containerColor = Color(0xFF38452D),
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, bottom = 68.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = stringResource(R.string.title_template),
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
            )
            Spacer(modifier = Modifier.height(25.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    16.dp,
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PatternState.entries.forEach { pattern ->
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(130.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF2C3520)
                        ),
                        border = if (currentPatternState == pattern) BorderStroke(
                            2.dp,
                            Color.White
                        ) else null,
                        onClick = {
                            onIntent(ChoosePattern(pattern))
                        },
                        content = {
                            Box(
                                modifier = Modifier
                                    .padding(top = 12.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color(0xFF122505))
                                    .align(Alignment.CenterHorizontally),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    modifier = Modifier.padding(10.dp),
                                    text = stringResource(pattern.titleRes),
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = Color(0xFFFFFFFF),
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}
