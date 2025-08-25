package com.harsh.notes.car

import android.app.Presentation
import android.content.Intent
import android.graphics.Rect
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.SurfaceView
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.car.app.AppManager
import androidx.car.app.CarAppService
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.Session
import androidx.car.app.SessionInfo
import androidx.car.app.SurfaceCallback
import androidx.car.app.SurfaceContainer
import androidx.car.app.model.Action
import androidx.car.app.model.ActionStrip
import androidx.car.app.model.Template
import androidx.car.app.navigation.model.NavigationTemplate
import androidx.car.app.validation.HostValidator
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner

/*
Todo - under development
 */
class NotesCarService : CarAppService() {
    override fun createHostValidator(): HostValidator {
        return HostValidator.ALLOW_ALL_HOSTS_VALIDATOR
    }

    override fun onCreateSession(sessionInfo: SessionInfo): Session {
        return NotesCarSession()
    }
}

class NotesCarSession : Session(), SavedStateRegistryOwner {

    val savedStateOwner = MySavedStateOwner()

    override fun onCreateScreen(intent: Intent): Screen {
        val surfaceCallback = HelloWorldSurfaceCallback(carContext, this)
        val appManager = carContext.getCarService(AppManager::class.java)
        appManager.setSurfaceCallback(surfaceCallback)
        return NotesScreen(this, carContext)
    }

    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateOwner.savedStateRegistry

}

class NotesScreen(private val carSession: NotesCarSession, private val carContext: CarContext) : Screen(carContext) {

    override fun onGetTemplate(): Template {
        // Example notes (replace with your DB / repository)
//        val notes = listOf("Buy groceries", "Meeting at 3 PM", "Call mom")
//
//        val itemListBuilder = ItemList.Builder()
//        notes.forEach { note ->
//            Log.d("NotesScreen", "Adding note item: $note")
//            itemListBuilder.addItem(
//                Row.Builder()
//                    .setTitle(note)
//                    .addText("Tap to open")
//                    .setOnClickListener {
//                        // Handle note click (e.g. show details)
//                    }
//                    .build()
//            )
//        }


//        return ListTemplate.Builder()
//            .setSingleList(itemListBuilder.build())
//            .setTitle("My Notes")
//            .build()

//        return MessageTemplate.Builder("Rendering Compose on Surface…")
//            .setTitle("Compose Surface")
//            .build()

        val actionStrip = ActionStrip.Builder()
            .addAction(
                Action.Builder()
                    .setTitle("Exit")
                    .setOnClickListener {
                        carContext.finishCarApp()
                    }
                    .build()
            )
            .build()
        return NavigationTemplate.Builder().setActionStrip(actionStrip)
            .build()
    }
}

@Composable
fun MyCarComposeUI() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Blue),
        contentAlignment = Alignment.Center
    ) {
        CameraPreview()
    }
}


@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
) {
    val context = LocalContext.current

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            // SurfaceView for CameraX
            val surfaceView = SurfaceView(ctx)

            val preview = Preview.Builder().build()

            val surfaceProvider = Preview.SurfaceProvider { request ->
                val surface = surfaceView.holder.surface
                if (surface != null && surface.isValid) {
                    request.provideSurface(
                        surface,
                        ContextCompat.getMainExecutor(ctx)
                    ) { result ->
                        Log.d("CameraX", "Surface released: $result")
                    }
                }
            }

            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview
                    )
                    preview.setSurfaceProvider(surfaceProvider)
                } catch (exc: Exception) {
                    Log.e("CameraX", "Use case binding failed", exc)
                }
            }, ContextCompat.getMainExecutor(ctx))

            surfaceView
        }
    )
}

class MySavedStateOwner : SavedStateRegistryOwner {
    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    init {
        lifecycleRegistry.currentState = Lifecycle.State.INITIALIZED
        savedStateRegistryController.performRestore(null)
    }

    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry
    override val lifecycle: Lifecycle
        get() = lifecycleRegistry
}

class HelloWorldSurfaceCallback(private val context: CarContext, private val carSession: NotesCarSession) : SurfaceCallback {
    lateinit var virtualDisplay: VirtualDisplay
    lateinit var presentation: Presentation

    override fun onSurfaceAvailable(surfaceContainer: SurfaceContainer) {
        virtualDisplay = context
            .getSystemService(DisplayManager::class.java)
            .createVirtualDisplay(
                "testing" ,
                surfaceContainer.width,
                surfaceContainer.height,
                160,
                surfaceContainer.surface,
                0
            )
        presentation = Presentation(context, virtualDisplay.display)

        // Instantiate the view to be used as the content view
        val view = ComposeView(presentation.window!!.context)

        view.setViewTreeLifecycleOwner(carSession)
        view.setViewTreeSavedStateRegistryOwner(carSession)
        view.setContent {
            MyCarComposeUI()
        }
        presentation.setContentView(view)
        presentation.show()
    }

    override fun onSurfaceDestroyed(surfaceContainer: SurfaceContainer) {
        Handler(Looper.getMainLooper()).post {
            presentation.dismiss()
            virtualDisplay.release()
        }
    }

    override fun onVisibleAreaChanged(visibleArea: Rect) {
        super.onVisibleAreaChanged(visibleArea)
    }
}