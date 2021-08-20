package levkaantonov.com.study.weatherapp.screens.common

import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.LifecycleOwner

class PermissionRequestDialog(
    activityResultRegistry: ActivityResultRegistry,
    lifecycleOwner: LifecycleOwner,
    callback: (permissions: MutableMap<String, Boolean>) -> Unit
) {
    private val permissionRequest = activityResultRegistry.register(
        REG_KEY,
        lifecycleOwner,
        ActivityResultContracts.RequestMultiplePermissions(), callback
    )

    fun requestPermissions(
        requestedPermissions: Array<String>
    ) {
        permissionRequest.launch(requestedPermissions)
    }

    private companion object {
        private const val REG_KEY = "PermissionRequestDialog"
    }
}