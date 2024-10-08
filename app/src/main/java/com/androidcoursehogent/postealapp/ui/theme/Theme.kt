package com.androidcoursehogent.postealapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,

    //Pruebita para la barra
    background = Color(0xFF1F1E1E),
    surface = Color(0xFF1F1E1E), // Color para el fondo de la barra en modo oscuro
    onSurface = Color(0xFFBB86FC), // Color para ícono seleccionado en modo oscuro
    onSurfaceVariant = Color(0xFF888888), // Color para ícono no seleccionado en modo oscuro
    onSecondary = Color.Red,
    onTertiary = Color.Gray
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,

    //Pruebita para la barra
    surface = Color(0xFFE0E0E0),
    background = Color(0xFFE0E0E0),// Color para el fondo de la barra en modo claro
    onSurface = Color(0xFF6200EE), // Color para ícono seleccionado en modo claro
    onSurfaceVariant = Color(0xFF888888), // Color para ícono no seleccionado en modo claro
    onSecondary = Color(0xFFAA1757),
    onTertiary = Color.Gray

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun PostealappTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val typography = if (darkTheme) {
        DarkTypography
    } else {
        LightTypography
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}