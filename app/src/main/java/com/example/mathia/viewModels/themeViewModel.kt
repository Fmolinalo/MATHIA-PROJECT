package com.example.mathia.viewModels


import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


enum class  ThemeMode {
    LIGHT,
    DARK,
    SYSTEM
}

 class ThemeViewModel : ViewModel(){

     private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
     val themeMode: StateFlow<ThemeMode> = _themeMode

     fun cambiarTema(nuevoModo: ThemeMode) {
         _themeMode.value = nuevoModo
     }
 }