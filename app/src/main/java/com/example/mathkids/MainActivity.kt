package com.example.mathkids

import android.os.Bundle
import com.example.mathkids.data.AppDatabase
import com.example.mathkids.data.StudentEntity
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.room.Room
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.example.mathkids.ui.theme.MathkidsTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MathkidsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val context = LocalContext.current
                    val coroutineScope = rememberCoroutineScope()

                    val db = remember {
                        Room.databaseBuilder(
                            context,
                            AppDatabase::class.java, "matekids-db"
                        ).build()
                    }

                    var startDestination by remember { mutableStateOf("loading") }
                    var savedName by remember { mutableStateOf("") }

                    LaunchedEffect(Unit) {
                        val existingStudent = withContext(Dispatchers.IO) {
                            db.studentDao().getStudentProfile()
                        }

                        if (existingStudent != null) {
                            savedName = existingStudent.nombre
                            startDestination = "main_menu/$savedName"
                        } else {
                            startDestination = "welcome"
                        }
                    }

                    if (startDestination == "loading") {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Color(0xFF9C27B0))
                        }
                    } else {
                        NavHost(navController = navController, startDestination = startDestination) {
                            composable("welcome") {
                                WelcomeScreen(
                                    onStartClicked = { navController.navigate("create_profile") },
                                    onLoginClicked = { navController.navigate("login_panel") },
                                    onRegisterClicked = { navController.navigate("create_profile") }
                                )
                            }

                            composable("create_profile") {
                                CreateProfileScreen(
                                    onProfileCreated = { nombreIngresado ->
                                        coroutineScope.launch(Dispatchers.IO) {
                                            val nuevoEstudiante = StudentEntity(
                                                nombre = nombreIngresado,
                                                edad = 6,
                                                grado = 1,
                                                avatar_id = 1
                                            )
                                            db.studentDao().insertStudent(nuevoEstudiante)

                                            withContext(Dispatchers.Main) {
                                                navController.navigate("main_menu/$nombreIngresado") {
                                                    popUpTo("welcome") { inclusive = true }
                                                }
                                            }
                                        }
                                    }
                                )
                            }

                            composable(
                                route = "main_menu/{userName}",
                                arguments = listOf(navArgument("userName") { type = NavType.StringType })
                            ) { backStackEntry ->
                                val user = backStackEntry.arguments?.getString("userName") ?: "Explorador"
                                MainMenuScreen(
                                    userName = user,
                                    onSumaClicked = { navController.navigate("game_screen/$user/Suma") },
                                    onRestaClicked = { navController.navigate("game_screen/$user/Resta") }
                                )
                            }

                            composable(
                                route = "game_screen/{userName}/{operation}",
                                arguments = listOf(
                                    navArgument("userName") { type = NavType.StringType },
                                    navArgument("operation") { type = NavType.StringType }
                                )
                            ) { backStackEntry ->
                                val userName = backStackEntry.arguments?.getString("userName") ?: ""
                                val operation = backStackEntry.arguments?.getString("operation") ?: "Suma"

                                GameScreen(
                                    userName = userName,
                                    operation = operation,
                                    onBack = { navController.popBackStack() }
                                )
                            }

                            composable("game_main") {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("¡Pantalla del Juego MathQuest en construcción!")
                                }
                            }

                            composable("login_panel") {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("¡Panel de Login para Profesores!")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun WelcomeScreen(
        onStartClicked: () -> Unit,
        onLoginClicked: () -> Unit,
        onRegisterClicked: () -> Unit
    ) {
        val backgroundGradient = Brush.verticalGradient(
            colors = listOf(Color(0xFFF3E5F5), Color(0xFFFCE4EC))
        )

        val context = LocalContext.current
        val imageLoader = ImageLoader.Builder(context)
            .components {
                if (android.os.Build.VERSION.SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundGradient)
                .systemBarsPadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🧮", fontSize = 40.sp)
                        Text(
                            text = "MathIA",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF7B1FA2)
                        )
                        Text(
                            text = "✨ ¡Aprende mates jugando! ✨",
                            fontSize = 14.sp,
                            color = Color.Black
                        )
                    }
                }

                Box(contentAlignment = Alignment.Center) {
                    Column(Modifier.offset(x = (-110).dp)) {
                        repeat(3) {
                            Box(
                                Modifier
                                    .size(25.dp, 6.dp)
                                    .background(Color(0xFFFF4081), RoundedCornerShape(5.dp))
                                    .padding(2.dp)
                            )
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                    Column(Modifier.offset(x = 110.dp)) {
                        repeat(3) {
                            Box(
                                Modifier
                                    .size(25.dp, 6.dp)
                                    .background(Color(0xFFFF4081), RoundedCornerShape(5.dp))
                                    .padding(2.dp)
                            )
                            Spacer(Modifier.height(8.dp))
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(180.dp)
                            .background(Color(0xFFFF80AB), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            AsyncImage(
                                model = R.drawable.ajolote, // Tu archivo en res/drawable
                                contentDescription = "Mateo te saluda",
                                imageLoader = imageLoader,
                                modifier = Modifier.size(220.dp) // Un poco más grande para la bienvenida
                            )

                            Spacer(Modifier.height(24.dp))

                            Text(
                                text = "¡Bienvenidos a\nMathKids!",
                                fontSize = 36.sp,
                                fontWeight = FontWeight.ExtraBold,
                                textAlign = TextAlign.Center,
                                color = Color(0xFF2D3436),
                                lineHeight = 40.sp
                            )

                            Text(
                                text = "Aprende matemáticas jugando con Mateo",
                                fontSize = 16.sp,
                                color = Color.Black, // Siguiendo tu regla de texto negro
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                    Text(
                        "😊",
                        fontSize = 24.sp,
                        modifier = Modifier.align(Alignment.TopCenter).offset(y = (-10).dp)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White,
                        shadowElevation = 2.dp,
                        modifier = Modifier.padding(bottom = 32.dp)
                    ) {
                        Text(
                            text = "¡Hola! Soy Mateo, tu amigo ajolote.\n¡Vamos a aprender matemáticas juntos!",
                            modifier = Modifier.padding(16.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 15.sp,
                            lineHeight = 20.sp
                        )
                    }

                //    Button(
                 //       onClick = onStartClicked,
                 //       modifier = Modifier.fillMaxWidth().height(60.dp),
                 //       shape = RoundedCornerShape(18.dp),
                 //       colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                  //      contentPadding = PaddingValues()
                 //   ) {
                 //       Box(
                 //           modifier = Modifier
                 //               .fillMaxSize()
                 //               .background(
          //                          Brush.horizontalGradient(listOf(Color(0xFF00E676), Color(0xFF00B0FF))),
                  //                  RoundedCornerShape(18.dp)
                //                ),
                 //           contentAlignment = Alignment.Center
                //        ) {
                        //    Text("▷  Comenzar", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
              //          }
                 //   }

                    Spacer(Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = onLoginClicked,
                        modifier = Modifier.fillMaxWidth().height(60.dp),
                        shape = RoundedCornerShape(18.dp),
                        border = ButtonDefaults.outlinedButtonBorder.copy(width = 2.dp)
                    ) {
                        Text("→]  Iniciar Sesión", color = Color.Black, fontSize = 18.sp)
                    }

                    Spacer(Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = onRegisterClicked,
                        modifier = Modifier.fillMaxWidth().height(60.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C27B0))
                    ) {
                        Text(" + Crear Nuevo Perfil", color = Color.White, fontSize = 18.sp)
                    }
                }
            }
        }
    }

    @Composable
    fun CreateProfileScreen(onProfileCreated: (String) -> Unit) {
        var name by remember { mutableStateOf("") }
        var selectedAge by remember { mutableStateOf(6) }
        var selectedGrade by remember { mutableStateOf(1) }
        var selectedAvatar by remember { mutableStateOf(0) }

        val avatars = listOf("🦸‍♂️", "👨‍🚀", "🧙‍♀️", "🦄", "🐉", "🦊", "🐼", "🦁", "🐯", "🐨")

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Crear Perfil", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2D3436))

            Spacer(Modifier.height(24.dp))

            Text("¿Cómo te llamas?", Modifier.align(Alignment.Start), fontWeight = FontWeight.SemiBold)
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = { Text(text = "Escribe tu nombre", color = Color.Black) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color(0xFF9C27B0),
                    unfocusedBorderColor = Color.Black,
                    cursorColor = Color(0xFF9C27B0)
                ),
                singleLine = true
            )

            Spacer(Modifier.height(20.dp))

            Text("¿Cuántos años tienes?", Modifier.align(Alignment.Start), fontWeight = FontWeight.SemiBold)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                listOf(6, 7, 8).forEach { age ->
                    MateKidsChip(text = "$age años", isSelected = selectedAge == age) { selectedAge = age }
                }
            }

            Spacer(Modifier.height(20.dp))

            Text("¿En qué grado estás?", Modifier.align(Alignment.Start), fontWeight = FontWeight.SemiBold)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                listOf(1, 2, 3).forEach { grade ->
                    MateKidsChip(text = "$grade°", isSelected = selectedGrade == grade) { selectedGrade = grade }
                }
            }

            Spacer(Modifier.height(20.dp))

            Text("Elige tu avatar", Modifier.align(Alignment.Start), fontWeight = FontWeight.SemiBold)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                avatars.forEachIndexed { index, emoji ->
                    AvatarBox(emoji = emoji, isSelected = selectedAvatar == index) { selectedAvatar = index }
                }
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = { onProfileCreated(name) },
                modifier = Modifier.fillMaxWidth().height(60.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDFE6E9)),
                enabled = name.isNotBlank()
            ) {
                Text("Continuar  →", color = Color.Black, fontSize = 20.sp)
            }
        }
    }

    @Composable
    fun MateKidsChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isSelected) Color(0xFFE84393) else Color(0xFFF1F2F6)
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.width(100.dp)
        ) {
            Text(text, color = if (isSelected) Color.White else Color.Black)
        }
    }

    @Composable
    fun MainMenuScreen(
        userName: String,
        onSumaClicked: () -> Unit,
        onRestaClicked: () -> Unit
    ) {
        val context = LocalContext.current
        val imageLoader = ImageLoader.Builder(context)
            .components {
                if (android.os.Build.VERSION.SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FE))
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(50.dp).background(Color(0xFFFFD54F), CircleShape), contentAlignment = Alignment.Center) {
                        Text("🦁", fontSize = 28.sp)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(text = "¡Hola, $userName!", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text(text = "Nivel 5 • 142 estrellas", fontSize = 14.sp, color = Color.Black)
                    }
                }
                Button(
                    onClick = { /* Premios */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("🏆 Premios", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFAF0)),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("🔥", fontSize = 32.sp)
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text("¡Racha de 7 días!", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFFE65100))
                        Text("Sigue así para mantener tu racha", fontSize = 14.sp)
                    }
                }
            }

            Spacer(Modifier.height(30.dp))

            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {

                AsyncImage(
                    model = R.drawable.ajolote, // Asegúrate que se llame así en la carpeta drawable
                    contentDescription = "Mateo el ajolote animado",
                    imageLoader = imageLoader, // Le pasamos la configuración que hicimos arriba
                    modifier = Modifier.size(150.dp) // Ajusta el tamaño a tu gusto
                )

                Spacer(Modifier.height(10.dp))

                Surface(shape = RoundedCornerShape(16.dp), color = Color.White, shadowElevation = 2.dp) {
                    Text(
                        "¡Bienvenido! Hoy vamos a aprender\nalgo nuevo. ¿Listo para comenzar?",
                        Modifier.padding(15.dp), textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(Modifier.height(30.dp))

            Text("Tus Desafíos", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(bottom = 12.dp))

            MenuOptionItem(
                icon = "📈",
                title = "Mejorando en sumas",
                subtitle = "¡Nivel Intermedio!",
                color = Color(0xFFE8F5E9),
                onClick = onSumaClicked
            )

            MenuOptionItem(
                icon = "🎯",
                title = "Recomendado: Restas básicas",
                subtitle = "Repasa lo aprendido",
                color = Color(0xFFFFF3E0),
                onClick = onRestaClicked
            )

            MenuOptionItem(
                icon = "🕒",
                title = "Mejor hora: mañanas",
                subtitle = "Estadísticas de hoy",
                color = Color(0xFFE3F2FD),
                onClick = {}
            )
        }
    }

    @Composable
    fun MenuOptionItem(icon: String, title: String, subtitle: String, color: Color, onClick: () -> Unit) {
        Card(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(1.dp)
        ) {
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(45.dp).background(color, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                    Text(icon, fontSize = 20.sp)
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    Text(subtitle, fontSize = 12.sp, color = Color.Black)
                }
            }
        }
    }

    @Composable
    fun AvatarBox(emoji: String, isSelected: Boolean, onClick: () -> Unit) {
        Surface(
            onClick = onClick,
            shape = RoundedCornerShape(16.dp),
            color = if (isSelected) Color(0xFFFFD93D) else Color(0xFFF1F2F6),
            modifier = Modifier.size(60.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(emoji, fontSize = 30.sp)
            }
        }
    }
}

@Composable
fun GameScreen(userName: String, operation: String, onBack: () -> Unit) {
    var num1 by remember { mutableStateOf((1..10).random()) }
    var num2 by remember { mutableStateOf((1..10).random()) }
    var userAnswer by remember { mutableStateOf("") }
    var score by remember { mutableStateOf(0) }
    var message by remember { mutableStateOf("¡Resuelve el desafío!") }

    val generateExercise = {
        userAnswer = ""
        if (operation == "Suma") {
            num1 = (1..10).random()
            num2 = (1..10).random()
        } else {
            num1 = (5..15).random()
            num2 = (1..num1).random()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF0F4C3)).padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            IconButton(onClick = onBack) { Text("⬅️", fontSize = 24.sp) }
            Text("Estudiante: $userName", fontWeight = FontWeight.Bold)
            Text("Puntos: $score ⭐", color = Color(0xFFFBC02D), fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(40.dp))

        Card(
            modifier = Modifier.fillMaxWidth().height(200.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(Modifier.fillMaxSize(), Arrangement.Center, Alignment.CenterHorizontally) {
                Text(
                    text = if (operation == "Suma") "$num1 + $num2 =" else "$num1 - $num2 =",
                    fontSize = 64.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF37474F)
                )
                Text(
                    text = if (userAnswer.isEmpty()) "?" else userAnswer,
                    fontSize = 48.sp,
                    color = if (userAnswer.isEmpty()) Color.Black else Color(0xFF9C27B0)
                )
            }
        }

        Text(message, Modifier.padding(16.dp), color = Color.Black, fontWeight = FontWeight.Medium)

        Spacer(Modifier.weight(1f))

        val buttons = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "C", "0", "OK")

        BoxWithConstraints {
            val cellWidth = maxWidth / 3
            Column {
                for (i in 0 until 4) {
                    Row {
                        for (j in 0 until 3) {
                            val btnText = buttons[i * 3 + j]
                            NumberButton(btnText, cellWidth - 8.dp) {
                                when (btnText) {
                                    "C" -> userAnswer = ""
                                    "OK" -> {
                                        val correctResult = if (operation == "Suma") num1 + num2 else num1 - num2
                                        if (userAnswer.toIntOrNull() == correctResult) {
                                            score += 10
                                            message = "¡Excelente $userName! 🎉"
                                            generateExercise()
                                        } else {
                                            message = "¡Casi! Intenta de nuevo 🤔"
                                            userAnswer = ""
                                        }
                                    }
                                    else -> if (userAnswer.length < 2) userAnswer += btnText
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NumberButton(text: String, width: androidx.compose.ui.unit.Dp, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.padding(4.dp).size(width, 70.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = when(text) {
                "OK" -> Color(0xFF4CAF50)
                "C" -> Color(0xFFE57373)
                else -> Color.White
            },
            contentColor = if (text == "OK" || text == "C") Color.White else Color.Black
        ),
        elevation = ButtonDefaults.buttonElevation(4.dp)
    ) {
        Text(text, fontSize = 24.sp, fontWeight = FontWeight.Bold)
    }
}
