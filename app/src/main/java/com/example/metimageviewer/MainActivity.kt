package com.example.metimageviewer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
  private val vm by viewModels<MainViewModel>()
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      MaterialTheme {
        Surface(Modifier.fillMaxSize()) {
          MainScreen(vm)
        }
      }
    }
  }
}

@Composable
fun MainScreen(vm: MainViewModel) {
  val images by vm.images.collectAsState()
  val isLoading by vm.isLoading.collectAsState()
  val scope = rememberCoroutineScope()

  Column(Modifier.fillMaxSize().padding(16.dp)) {
    // show how many loaded
    Text("Loaded: ${images.size}", style = MaterialTheme.typography.subtitle1)

    Spacer(Modifier.height(8.dp))
    Button(
      onClick = { scope.launch { vm.loadImages() } },
      modifier = Modifier.fillMaxWidth(),
      enabled = !isLoading
    ) {
      if (isLoading) {
        CircularProgressIndicator(
          modifier = Modifier.size(20.dp),
          strokeWidth = 2.dp,
          color = MaterialTheme.colors.onPrimary
        )
        Spacer(Modifier.width(8.dp))
        Text("Loading…")
      } else {
        Text("Load MET Images")
      }
    }

    Spacer(Modifier.height(16.dp))
    LazyColumn {
      items(images) { img ->
        AsyncImage(
          model = img.primaryImageSmall,
          contentDescription = img.title,
          modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
        )
        Text(
          text = img.title,
          modifier = Modifier.padding(8.dp),
          style = MaterialTheme.typography.h6
        )
        Text(
          text = img.creditLine.orEmpty(),
          modifier = Modifier.padding(horizontal = 8.dp),
          style = MaterialTheme.typography.body2
        )
        Spacer(Modifier.height(12.dp))
      }
    }
  }
}
