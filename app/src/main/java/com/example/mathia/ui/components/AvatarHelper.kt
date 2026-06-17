package com.example.mathia.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

object AvatarHelper {
    fun getAvatarIcon(avatarKey: String): ImageVector {
        return when (avatarKey) {
            "👶", "default" -> Icons.Default.Face
            "🦸", "superhero" -> Icons.Default.Shield
            "🚀", "rocket", "avatar_rocket" -> Icons.Default.RocketLaunch
            "🧙", "wizard", "avatar_wizard" -> Icons.Default.AutoAwesome
            "🦄", "unicorn", "avatar_unicorn" -> Icons.Default.Pets
            "🐉", "dragon" -> Icons.Default.CrueltyFree
            "🦊", "fox" -> Icons.Default.Pets
            "🐼", "panda" -> Icons.Default.Pets
            "🦁", "lion" -> Icons.Default.Pets
            "🐯", "tiger" -> Icons.Default.Pets
            "🐨", "koala" -> Icons.Default.Pets
            "🦖", "dino", "avatar_dino" -> Icons.Default.Pets
            "👾", "alien", "avatar_alien" -> Icons.Default.SmartToy
            "👑", "crown", "avatar_crown" -> Icons.Default.MilitaryTech
            "👧", "🧒", "👧🏻", "🧑", "🐣" -> Icons.Default.Face
            else -> Icons.Default.Person
        }
    }

    fun getThemeIcon(themeVisual: String): ImageVector {
        return Icons.Default.Palette
    }
}

@Composable
fun AvatarIcon(
    avatarKey: String,
    modifier: Modifier = Modifier,
    tint: Color = Color.Unspecified
) {
    Icon(
        imageVector = AvatarHelper.getAvatarIcon(avatarKey),
        contentDescription = "Avatar",
        tint = tint,
        modifier = modifier
    )
}
