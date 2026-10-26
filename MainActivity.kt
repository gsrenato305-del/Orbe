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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Bg = Color(0xFF0A0A0C)

data class Movie(
    val title: String, val type: String, val year: String, val genres: String, val stars: Int,
    val desc: String, val nota: Float, val url: String, val c1: Color, val c2: Color
)

private const val V = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/"
private const val DEMO = "Conteúdo de demonstração para testar o player do Orbe."

private val filmes = listOf(
    Movie("Big Buck Bunny", "FILME", "2008", "Animação, Comédia", 4, "Um coelho gigante e gentil decide dar o troco em três roedores que o provocam.", 7.6f, V + "BigBuckBunny.mp4", Color(0xFF4E8A3E), Color(0xFF0F1E0C)),
    Movie("Sintel", "FILME", "2010", "Animação, Fantasia", 4, "Uma jovem parte em busca de um dragão filhote que ela adotou e perdeu.", 7.4f, V + "Sintel.mp4", Color(0xFFC97B3A), Color(0xFF1A0E08)),
    Movie("Tears of Steel", "FILME", "2012", "Ficção Científica", 3, "Guerreiros e cientistas tentam salvar o mundo de robôs que dominam a cidade.", 6.5f, V + "TearsOfSteel.mp4", Color(0xFF3A7BA8), Color(0xFF0A141C)),
    Movie("Elephants Dream", "FILME", "2006", "Animação, Surreal", 3, "Dois personagens exploram uma máquina gigante e misteriosa.", 6.0f, V + "ElephantsDream.mp4", Color(0xFF7A4BB0), Color(0xFF140C20)),
)
private val series = listOf(
    Movie("Fugas", "SÉRIE", "2024", "Demonstração", 3, DEMO, 6.0f, V + "ForBiggerEscapes.mp4", Color(0xFF5A6A7A), Color(0xFF12161C)),
    Movie("Explosões", "SÉRIE", "2024", "Demonstração", 3, DEMO, 6.0f, V + "ForBiggerBlazes.mp4", Color(0xFFB5502B), Color(0xFF200E08)),
    Movie("Diversão", "SÉRIE", "2024", "Demonstração", 3, DEMO, 6.0f, V + "ForBiggerFun.mp4", Color(0xFF2B9C98), Color(0xFF0A1A1C)),
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { OrbeApp() }
    }
}

@Composable
fun OrbeApp() {
    var open by remember { mutableStateOf<Movie?>(null) }
    var playing by remember { mutableStateOf(false) }
    Box(Modifier.fillMaxSize().background(Bg)) {
        val m = open
        if (m == null) Home { open = it }
        else if (playing) Player(m) { playing = false }
        else Details(m, { open = null }, { playing = true })
    }
}

@Composable
fun Focusable(
    modifier: Modifier = Modifier, shape: RoundedCornerShape = RoundedCornerShape(14.dp),
    onFocus: () -> Unit = {}, onClick: () -> Unit = {}, content: @Composable BoxScope.(Boolean) -> Unit
) {
    var f by remember { mutableStateOf(false) }
    val s by animateFloatAsState(if (f) 1.06f else 1f, label = "scale")
    Box(
        modifier.scale(s).clip(shape)
            .border(if (f) 3.dp else 0.dp, if (f) Color.White else Color.Transparent, shape)
            .onFocusChanged { st -> f = st.isFocused; if (st.isFocused) onFocus() }
            .clickable(onClick = onClick),
    ) { content(f) }
}

@Composable
fun Backdrop(m: Movie, dim: Boolean = false) {
    Box(Modifier.fillMaxSize().background(Brush.linearGradient(listOf(m.c1, m.c2))))
    Box(Modifier.fillMaxSize().background(Brush.horizontalGradient(listOf(Bg.copy(if (dim) .95f else .85f), Color.Transparent))))
    Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Transparent, Bg))))
}

@Composable
fun TopBar() = Row(Modifier.fillMaxWidth().padding(horizontal = 48.dp, vertical = 20.dp), verticalAlignment = Alignment.CenterVertically) {
    Focusable(shape = CircleShape) { _ -> Icon(Icons.Default.Search, null, Modifier.padding(8.dp).size(26.dp), tint = Color.White) }
    Spacer(Modifier.weight(1f))
    listOf("Início", "Filmes", "Séries", "Apps").forEachIndexed { i, t ->
        Focusable(shape = RoundedCornerShape(50)) { _ ->
            Text(t, Modifier.padding(horizontal = 20.dp, vertical = 8.dp), color = Color.White.copy(if (i == 0) 1f else .6f),
                fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
    Spacer(Modifier.weight(1.3f))
}

@Composable
fun MetaRow(m: Movie) = Row(verticalAlignment = Alignment.CenterVertically) {
    Text(m.type, Modifier.border(1.dp, Color.White.copy(.6f), RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp),
        color = Color.White.copy(.8f), fontSize = 12.sp)
    Spacer(Modifier.width(10.dp))
    Text("${m.year} • ${m.genres} • " + "★".repeat(m.stars) + "☆".repeat(5 - m.stars), color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
}

@Composable
fun Home(onOpen: (Movie) -> Unit) {
    var hero by remember { mutableStateOf(filmes[0]) }
    Box(Modifier.fillMaxSize()) {
        Backdrop(hero)
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            TopBar()
            Box(Modifier.fillMaxWidth().height(250.dp)) {
                Column(Modifier.align(Alignment.BottomStart).padding(start = 48.dp, bottom = 16.dp)) {
                    Text(hero.title.uppercase(), color = Color.White, fontSize = 50.sp, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Black)
                    Spacer(Modifier.height(6.dp)); MetaRow(hero); Spacer(Modifier.height(8.dp))
                    Text(hero.desc, Modifier.widthIn(max = 560.dp), color = Color.White.copy(.8f), fontSize = 15.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Spacer(Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.width(200.dp).height(3.dp).background(Color.White.copy(.25f))) {
                            Box(Modifier.fillMaxWidth(hero.nota / 10f).fillMaxHeight().background(Color.White))
                        }
                        Spacer(Modifier.width(12.dp))
                        Text("Nota " + "%.1f".format(hero.nota).replace('.', ','), color = Color.White.copy(.7f), fontSize = 13.sp)
                    }
                }
                Text("Disponível no Orbe", Modifier.align(Alignment.TopEnd).padding(top = 8.dp, end = 48.dp)
                    .border(1.5.dp, Color.White.copy(.8f), RoundedCornerShape(50)).padding(horizontal = 16.dp, vertical = 8.dp),
                    color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            Rail("Filmes", filmes, { hero = it }, onOpen)
            Rail("Séries", series, { hero = it }, onOpen)
        }
    }
}

@Composable
fun Rail(label: String, list: List<Movie>, onFocus: (Movie) -> Unit, onOpen: (Movie) -> Unit) {
    Text(label, Modifier.padding(start = 48.dp, top = 12.dp), color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp), contentPadding = PaddingValues(horizontal = 48.dp, vertical = 14.dp)) {
        items(list) { m -> MovieCard(m, { onFocus(m) }, { onOpen(m) }) }
    }
}

@Composable
fun MovieCard(m: Movie, onFocus: () -> Unit, onClick: () -> Unit) =
    Focusable(Modifier.size(200.dp, 112.dp), RoundedCornerShape(14.dp), onFocus, onClick) { _ ->
        Box(Modifier.fillMaxSize().background(Brush.linearGradient(listOf(m.c1, m.c2))))
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(.75f)))))
        Text(m.title, Modifier.align(Alignment.BottomStart).padding(10.dp), color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }

@Composable
fun Pill(label: String, white: Boolean, onClick: () -> Unit = {}, modifier: Modifier = Modifier) =
    Focusable(modifier, RoundedCornerShape(50), onClick = onClick) { _ ->
        Text(label, Modifier.background(if (white) Color.White else Color(0xFF2A2A2E)).padding(horizontal = 26.dp, vertical = 14.dp),
            color = if (white) Color.Black else Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
    }

@Composable
fun Details(m: Movie, onBack: () -> Unit, onPlay: () -> Unit) {
    val fr = remember { FocusRequester() }
    LaunchedEffect(Unit) { fr.requestFocus() }
    BackHandler(onBack = onBack)
    Box(Modifier.fillMaxSize()) {
        Backdrop(m, dim = true)
        Row(Modifier.fillMaxSize().padding(horizontal = 48.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(m.title.uppercase(), color = Color.White, fontSize = 48.sp, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Black)
                Spacer(Modifier.height(8.dp)); MetaRow(m); Spacer(Modifier.height(10.dp))
                Text(m.desc, Modifier.widthIn(max = 560.dp), color = Color.White.copy(.8f), fontSize = 16.sp)
                Spacer(Modifier.height(22.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Pill("▶ Assistir", true, onPlay, modifier = Modifier.focusRequester(fr))
                    Pill("+ Minha lista", false)
                    Pill("Voltar", false, onBack)
                }
            }
            Box(Modifier.size(150.dp, 225.dp).clip(RoundedCornerShape(14.dp)).background(Brush.linearGradient(listOf(m.c1, m.c2))), contentAlignment = Alignment.BottomCenter) {
                Text(m.title.uppercase(), Modifier.padding(10.dp), color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
fun Player(m: Movie, onBack: () -> Unit) {
    val ctx = LocalContext.current
    val player = remember {
        ExoPlayer.Builder(ctx).build().apply { setMediaItem(MediaItem.fromUri(m.url)); prepare(); playWhenReady = true }
    }
    DisposableEffect(Unit) { onDispose { player.release() } }
    BackHandler(onBack = onBack)
    AndroidView(
        factory = { c -> PlayerView(c).apply { this.player = player; useController = true; isFocusable = true; post { requestFocus() } } },
        modifier = Modifier.fillMaxSize().background(Color.Black)
    )
}
