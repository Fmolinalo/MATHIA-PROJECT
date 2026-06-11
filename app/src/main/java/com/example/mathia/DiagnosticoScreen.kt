package com.example.mathia

import androidx.compose.runtime.Composable

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