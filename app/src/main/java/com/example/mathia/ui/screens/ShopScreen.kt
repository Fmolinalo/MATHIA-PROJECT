package com.example.mathia.ui.screens

import android.provider.CalendarContract
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Star
import com.example.mathia.AppColors
import com.example.mathia.StudentViewModel
import com.example.mathia.model.AlertType
import com.example.mathia.model.MathiaAlert
import com.example.mathia.model.ShopItem
import com.example.mathia.model.Student
import com.example.mathia.ui.components.AvatarIcon
import com.example.mathia.ui.theme.GrayDinamico
import com.example.mathia.ui.theme.Purple2Dinamico
import com.example.mathia.ui.theme.PurpleDinamico

fun getThemeColor(visual: String): Color {
    return when (visual) {
        "💜" -> Color(0xFFD0BCFF)
        "💚" -> Color(0xFFA5D6A7)
        "💙" -> Color(0xFF90CAF9)
        "💛" -> Color(0xFFFFE082)
        "💖" -> Color(0xFFF48FB1)
        else -> Color(0xFF231640) // Navy default
    }
}

@Composable
fun ShopTabContent(
    student: Student,
    onUpdateStudent: (Student) -> Unit,
    onShowAlert: (MathiaAlert) -> Unit,
    viewModel: StudentViewModel
) {
    val context = LocalContext.current
    var selectedCategory by remember { mutableIntStateOf(0) } // 0 = Avatars, 1 = Themes

    val avatars = listOf(
        ShopItem("avatar_dino", "Dinosaurio", "🦖", 30, true),
        ShopItem("avatar_unicorn", "Unicornio", "🦄", 30, true),
        ShopItem("avatar_rocket", "Cohete", "🚀", 50, true),
        ShopItem("avatar_alien", "Alienígena", "👾", 50, true),
        ShopItem("avatar_wizard", "Mago", "🧙", 80, true),
        ShopItem("avatar_crown", "Corona Real", "👑", 100, true)
    )

    val themes = listOf(
        ShopItem("theme_lila", "Lila Clásico", "💜", 0, false),
        ShopItem("theme_mint", "Verde Menta", "💚", 40, false),
        ShopItem("theme_space", "Azul Espacial", "💙", 60, false),
        ShopItem("theme_sun", "Amarillo Sol", "💛", 80, false),
        ShopItem("theme_pink", "Rosa Algodón", "💖", 100, false)
    )

    val currentList = if (selectedCategory == 0) avatars else themes

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Balance Header Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Store,
                            contentDescription = null,
                            tint = Purple2Dinamico,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "La Tiendita de Mateo",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = Purple2Dinamico
                        )
                    }
                    Text(
                        text = "¡Usa tus estrellas para personalizar tu perfil!",
                        fontSize = 12.sp,
                        color = AppColors.Gray600
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .border(1.5.dp, AppColors.Amber.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = AppColors.Amber,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${student.stars}",
                        fontWeight = FontWeight.Black,
                        color = AppColors.Amber,
                        fontSize = 16.sp
                    )
                }
            }
        }

        // Category Tab selector
        TabRow(
            selectedTabIndex = selectedCategory,
            containerColor = Color.Transparent,
            contentColor = Purple2Dinamico,
            divider = {}
        ) {
            Tab(
                selected = selectedCategory == 0,
                onClick = { selectedCategory = 0 },
                text = { Text("Avatares", fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedCategory == 1,
                onClick = { selectedCategory = 1 },
                text = { Text("Temas de Fondo", fontWeight = FontWeight.Bold) }
            )
        }

        // Items Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(currentList) { item ->
                val isUnlocked = if (item.isAvatar) {
                    student.unlockedAvatars.contains(item.visual) || item.visual == "👶"
                } else {
                    student.unlockedThemes.contains(item.name) || item.name == "Lila Clásico"
                }

                val isEquipped = if (item.isAvatar) {
                    student.avatar == item.visual
                } else {
                    student.equippedTheme == item.name
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(2.dp, if (isEquipped) Purple2Dinamico else GrayDinamico)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(14.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Visual box
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(AppColors.Bg, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (item.isAvatar) {
                                AvatarIcon(avatarKey = item.visual, modifier = Modifier.size(36.dp), tint = PurpleDinamico)
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(getThemeColor(item.visual), CircleShape)
                                        .border(1.dp, GrayDinamico, CircleShape)
                                )
                            }
                        }

                        Text(
                            text = item.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )

                        if (isEquipped) {
                            // Equipped badge
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(36.dp)
                                    .background(Purple2Dinamico, RoundedCornerShape(50.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                  Text(
                                      text = "Puesto",
                                      color = GrayDinamico,
                                      fontWeight = FontWeight.Bold,
                                      fontSize = 12.sp
                                  )
                            }
                        } else if (isUnlocked) {
                            // Equip button
                            Button(
                                onClick = {
                                    val updatedStudent = if (item.isAvatar) {
                                        student.copy(avatar = item.visual)
                                    } else {
                                        student.copy(equippedTheme = item.name)
                                    }
                                    viewModel.actualizarCosmeticos(
                                        pin = student.pin,
                                        avatar = updatedStudent.avatar,
                                        theme = updatedStudent.equippedTheme
                                    ) { success ->
                                        if (success) {
                                            onUpdateStudent(updatedStudent)
                                            Toast.makeText(context, "¡Equipado!", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(36.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(50.dp)
                            ) {
                                Text("Poner", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        } else {
                            // Locked - Buy button
                            val canBuy = student.stars >= item.cost
                            Button(
                                onClick = {
                                    viewModel.comprarCosmetico(
                                        pin = student.pin,
                                        nuevoCosmetico = if (item.isAvatar) item.visual else item.name,
                                        esAvatar = item.isAvatar,
                                        costo = item.cost
                                    ) { success ->
                                        if (success) {
                                            val updatedUnlockedAvatars = if (item.isAvatar) {
                                                student.unlockedAvatars + item.visual
                                            } else student.unlockedAvatars

                                            val updatedUnlockedThemes = if (!item.isAvatar) {
                                                student.unlockedThemes + item.name
                                            } else student.unlockedThemes

                                            val updatedStudent = student.copy(
                                                stars = student.stars - item.cost,
                                                unlockedAvatars = updatedUnlockedAvatars,
                                                unlockedThemes = updatedUnlockedThemes
                                            )
                                            onUpdateStudent(updatedStudent)

                                            onShowAlert(
                                                MathiaAlert(
                                                    title = "¡Compra Exitosa!",
                                                    message = "¡Felicidades! Has desbloqueado ${item.name}. Ahora puedes ponértelo desde la tiendita.",
                                                    type = AlertType.SUCCESS,
                                                    buttonText = "¡Ver mi tienda!"
                                                )
                                            )
                                        } else {
                                            Toast.makeText(context, "Error en la compra", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                                enabled = canBuy,
                                modifier = Modifier.fillMaxWidth().height(36.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = AppColors.Pink,
                                    disabledContainerColor = GrayDinamico
                                ),
                                shape = RoundedCornerShape(50.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = if (canBuy) Color.White else AppColors.Gray400,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = "${item.cost}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (canBuy) Color.White else AppColors.Gray400
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

