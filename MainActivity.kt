package com.orbe.tv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Bg = Color(0xFF0E1224)
private val Purple = Color(0xFF8B5CF6)
private val Teal = Color(0xFF22B8CF)
private val Accent = Brush.horizontalGradient(listOf(Purple, Teal))
private val Glass = Color.White.copy(alpha = 0.10f)

data class Item(val title: String, val c1: Color, val c2: Color, val progress: Float? = null)

private val continuar = listOf(
    Item("SUSSURROS", Color(0xFF1B2A3A), Color(0xFF4A5A6A), .55f),
    Item("A JORNADA", Color(0xFFE9A85B), Color(0xFF3B2A1E), .30f),
    Item("HORIZONTE", Color(0xFFC98A3C), Color(0xFF2B2016), .10f),
    Item("THE LORS A", Color(0xFF3B3F9E), Color(0xFFB57BD6)),
    Item("ALÉM DO MAR", Color(0xFF2E6F8E), Color(0xFF14202B)),
)
private val populares = listOf(
    Item("", Color(0xFFCFE3E3), Color(0xFFE05A47)), Item("", Color(0xFF1E5AA8), Color(0xFF0B1E3D)),
    Item("", Color(0xFFD9602B), Color(0xFF7A1E1E)), Item("", Color(0xFF5E9BB5), Color(0xFF17303F)),
    Item("", Color(0xFF2B9C98), Color(0xFF1A3C4A)), Item("", Color(0xFF7B3FD0), Color(0xFF241046)),
    Item("", Color(0xFF3A7BA8), Color(0xFF0F2233)),
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { OrbeApp() }
    }
}

@Composable
fun OrbeApp() {
    var screen by remember { mutableStateOf("perfil") }
    Box(Modifier.fillMaxSize().background(Bg)) {
        when (screen) {
            "perfil" -> Profiles { screen = "home" }
            "home" -> Home { screen = "detalhes" }
            else -> { Home {}; Details(); BackHandler { screen = "home" } }
        }
    }
}

@Composable
fun Focusable(
    modifier: Modifier = Modifier, shape: RoundedCornerShape = RoundedCornerShape(18.dp),
    onClick: () -> Unit = {}, content: @Composable BoxScope.(Boolean) -> Unit
) {
    var f by remember { mutableStateOf(false) }
    val s by animateFloatAsState(if (f) 1.08f else 1f, label = "scale")
    Box(
        modifier.scale(s).clip(shape)
            .border(if (f) 3.dp else 0.dp, if (f) Brush.linearGradient(listOf(Teal, Purple)) else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent)), shape)
            .onFocusChanged { f = it.isFocused }.clickable(onClick = onClick),
    ) { content(f) }
}

@Composable
fun Logo() = Row(verticalAlignment = Alignment.CenterVertically) {
    Box(Modifier.size(52.dp).clip(CircleShape).background(Brush.radialGradient(listOf(Teal, Purple, Color(0xFF1A1040)))))
    Spacer(Modifier.width(12.dp))
    Text("Orbe", color = Color(0xFFBFD4F2), fontSize = 40.sp, fontWeight = FontWeight.Medium)
}

@Composable
fun TopBar() = Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
    Logo()
    Row(Modifier.clip(RoundedCornerShape(50)).background(Glass).padding(6.dp)) {
        listOf("Início", "Filmes", "Séries", "Categorias", "Minha Lista").forEachIndexed { i, t ->
            Focusable(shape = RoundedCornerShape(50)) { f ->
                val sel = i == 0 || f
                Text(t, Modifier.background(if (sel) Color(0xFFE6E8EF) else Color.Transparent).padding(horizontal = 24.dp, vertical = 10.dp),
                    color = if (sel) Color(0xFF111111) else Color.White.copy(.8f), fontSize = 20.sp)
            }
        }
    }
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        listOf(Icons.Default.Search, Icons.Default.Person).forEach { ic ->
            Focusable(shape = CircleShape) { Icon(ic, null, Modifier.background(Glass).padding(12.dp).size(26.dp), tint = Color.White) }
        }
    }
}

@Composable
fun Pill(label: String, icon: ImageVector, primary: Boolean, onClick: () -> Unit = {}) =
    Focusable(shape = RoundedCornerShape(50), onClick = onClick) {
        Row(Modifier.background(if (primary) Accent else Brush.linearGradient(listOf(Color(0xFF3A3F50), Color(0xFF3A3F50))))
            .padding(horizontal = 30.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = Color.White, modifier = Modifier.size(28.dp)); Spacer(Modifier.width(10.dp))
            Text(label, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Medium)
        }
    }

@Composable
fun Poster(item: Item, w: Int, h: Int, onClick: () -> Unit = {}) =
    Focusable(Modifier.size(w.dp, h.dp), onClick = onClick) { _ ->
        Box(Modifier.fillMaxSize().background(Brush.linearGradient(listOf(item.c1, item.c2))))
        Text(item.title, Modifier.align(Alignment.CenterStart).padding(16.dp), color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Black)
        val prog = item.progress
        if (prog != null) {
            Box(Modifier.align(Alignment.BottomCenter).padding(16.dp).fillMaxWidth().height(4.dp).clip(CircleShape).background(Color.White.copy(.25f))) {
                Box(Modifier.fillMaxWidth(prog).fillMaxHeight().background(Color.White))
            }
        }
    }

@Composable
fun Home(onDetails: () -> Unit) {
    Box(Modifier.fillMaxSize().background(Brush.radialGradient(listOf(Color(0xFF6A4CB0), Color.Transparent), center = androidx.compose.ui.geometry.Offset(1500f, 300f), radius = 1100f)))
    Column(Modifier.fillMaxSize().padding(horizontal = 48.dp, vertical = 28.dp)) {
        TopBar()
        Spacer(Modifier.height(56.dp))
        Text("◼ SÉRIE ORIGINAL", color = Color.White.copy(.6f), fontSize = 18.sp)
        Text("Odisseia Orbital", color = Color.White, fontSize = 52.sp, fontWeight = FontWeight.Bold)
        Text("Uma tripulação isolada no limite do sistema solar\nenfrenta o maior mistério da humanidade.", color = Color.White.copy(.7f), fontSize = 20.sp)
        Spacer(Modifier.height(12.dp))
        Text("Classificação  4.9", color = Color.White.copy(.7f), fontSize = 16.sp)
        Spacer(Modifier.height(18.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Pill("Assistir", Icons.Default.PlayArrow, true); Pill("Mais Informações", Icons.Default.Add, false, onDetails)
        }
        Spacer(Modifier.height(36.dp))
        Text("Continuar Assistindo", color = Color.White.copy(.85f), fontSize = 26.sp, fontWeight = FontWeight.Medium)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(28.dp), contentPadding = PaddingValues(vertical = 16.dp)) {
            items(continuar) { Poster(it, 340, 200, onDetails) }
        }
        Text("Populares no Orbe", color = Color.White.copy(.85f), fontSize = 26.sp, fontWeight = FontWeight.Medium)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(28.dp), contentPadding = PaddingValues(vertical = 16.dp)) {
            items(populares) { Poster(it, 250, 190) }
        }
    }
}

@Composable
fun Details() = Box(Modifier.fillMaxSize().background(Bg.copy(.55f)), contentAlignment = Alignment.Center) {
    Column(Modifier.width(900.dp).clip(RoundedCornerShape(28.dp))
        .background(Brush.linearGradient(listOf(Color(0xFF2A2F4A), Color(0xFF171B30)))).padding(40.dp)) {
        Text("Odisseia Orbital", color = Color.White, fontSize = 44.sp, fontWeight = FontWeight.Bold)
        Text("★★★★☆  4.3 · Gênero · Ano · 2020", color = Color(0xFFFFC53D), fontSize = 20.sp)
        Spacer(Modifier.height(12.dp))
        Text("Uma tripulação isolada no limite do sistema solar enfrenta o maior mistério da humanidade.", color = Color.White.copy(.7f), fontSize = 18.sp)
        Spacer(Modifier.height(20.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Pill("Assistir", Icons.Default.PlayArrow, true); Pill("Trailer", Icons.Default.PlayArrow, false)
        }
        Spacer(Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            listOf("Episódios", "Relacionados", "Elenco").forEachIndexed { i, t ->
                Focusable(shape = RoundedCornerShape(50)) { f ->
                    val sel = i == 0 || f
                    Text(t, Modifier.background(if (sel) Color(0xFFE6E8EF) else Color.Transparent).padding(horizontal = 22.dp, vertical = 10.dp),
                        color = if (sel) Color.Black else Color.White.copy(.8f), fontSize = 20.sp)
                }
            }
        }
        LazyRow(horizontalArrangement = Arrangement.spacedBy(20.dp), contentPadding = PaddingValues(vertical = 18.dp)) {
            items(listOf(Item("Episódio 1", Color(0xFF3A4A5A), Color(0xFF1B2430), .8f), Item("Episódio 2", Color(0xFF4A5A4A), Color(0xFF1B241B), .9f))) {
                Poster(it, 260, 150)
            }
        }
    }
}

@Composable
fun Profiles(onPick: () -> Unit) = Box(
    Modifier.fillMaxSize().background(Brush.linearGradient(listOf(Color(0xFF1B1840), Color(0xFF5B3FA8), Color(0xFF14284F)))),
    contentAlignment = Alignment.Center
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Logo(); Spacer(Modifier.height(40.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(40.dp)) {
            listOf("Ana" to Color(0xFFD16BA5), "Pedro" to Color(0xFF6B8FD1), "Kids" to Color(0xFF5BD19A), "Novo Perfil" to Color(0xFF555B70)).forEach { (n, c) ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Focusable(Modifier.size(130.dp), CircleShape, onPick) {
                        Box(Modifier.fillMaxSize().background(Brush.linearGradient(listOf(c, Purple))), contentAlignment = Alignment.Center) {
                            Icon(if (n == "Novo Perfil") Icons.Default.Add else Icons.Default.Person, null, tint = Color.White, modifier = Modifier.size(60.dp))
                        }
                    }
                    Spacer(Modifier.height(14.dp)); Text(n, color = Color.White, fontSize = 22.sp)
                }
            }
        }
    }
}
