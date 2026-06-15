package com.example.mathia.ui.screens

import androidx.compose.runtime.Composable
import com.example.mathia.StudentViewModel
import com.example.mathia.model.Student

@Composable
fun DiagnosticoScreen(
    student: Student,
    viewModel: StudentViewModel,
    onFinish: () -> Unit
) {
    AdaptiveExamScreen(
        student = student,
        onFinish = { _, _, _, _ ->
            onFinish()
        },
        viewModel = viewModel
    )
}
