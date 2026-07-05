package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.EmeraldGreen
import kotlinx.coroutines.launch

data class OnboardData(
    val title: String,
    val desc: String,
    val icon: ImageVector,
    val color: Color,
    val imageRes: Int? = null
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onFinished: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()

    val pages = listOf(
        OnboardData(
            title = "Deja de tirar tu dinero a la basura.",
            desc = "Cada alimento que vence es dinero perdido. Te ayudaremos a gestionar tu despensa para que nada termine en el cubo.",
            icon = Icons.Default.Savings,
            color = EmeraldGreen,
            imageRes = R.drawable.img_onboarding_pantry_1782847462558
        ),
        OnboardData(
            title = "Registra alimentos en segundos.",
            desc = "Añade productos de forma rápida. Controla cantidades, precios y fechas de forma visual y sencilla.",
            icon = Icons.Default.Speed,
            color = Color(0xFF3B82F6)
        ),
        OnboardData(
            title = "Nosotros te avisaremos antes.",
            desc = "Recibe recordatorios inteligentes para consumir lo más urgente. ¡Tu comida siempre fresca y tu bolsillo lleno!",
            icon = Icons.Default.NotificationsActive,
            color = Color(0xFFF59E0B)
        )
    )

    Scaffold(
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .navigationBarsPadding(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Indicadores
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(3) { index ->
                        Box(
                            modifier = Modifier
                                .size(if (pagerState.currentPage == index) 20.dp else 8.dp, 8.dp)
                                .clip(CircleShape)
                                .background(if (pagerState.currentPage == index) EmeraldGreen else Color.LightGray)
                        )
                    }
                }

                Button(
                    onClick = {
                        if (pagerState.currentPage < 2) {
                            scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                        } else {
                            onFinished()
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                ) {
                    Text(if (pagerState.currentPage == 2) "Empezar" else "Siguiente")
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.padding(start = 8.dp))
                }
            }
        }
    ) { padding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize().padding(padding)
        ) { pageIdx ->
            val page = pages[pageIdx]
            Column(
                modifier = Modifier.fillMaxSize().padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (page.imageRes != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth().height(250.dp).padding(bottom = 40.dp),
                        shape = RoundedCornerShape(32.dp)
                    ) {
                        Image(
                            painter = painterResource(id = page.imageRes),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(30.dp))
                            .background(page.color.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(page.icon, null, tint = page.color, modifier = Modifier.size(60.dp))
                    }
                    Spacer(modifier = Modifier.height(40.dp))
                }

                Text(
                    text = page.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    lineHeight = 34.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = page.desc,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
